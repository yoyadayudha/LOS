package com.example.demo.controller;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.service.LoanService;
import com.example.demo.validation.OnDraft;
import com.example.demo.validation.OnSubmit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Controller
public class LoanController {
    //lombok
    //dependency injector @Autowired

    private final Validator validator;
    private final LoanService loanService;

    public LoanController(Validator validator, LoanService loanService) {
        this.validator = validator;
        this.loanService = loanService;
    }

    private static final List<Integer> TENOR_OPTIONS = List.of(6, 12, 18, 24, 36, 60,120, 180);

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/applications/dashboard")
    public String dashboard(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam (defaultValue = "10") int size,
        Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isApprover = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().startsWith("ROLE_APPROVER"));

        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());

        Page<LoanResponseDTO> loanPage;


        if(isApprover){
            loanPage = loanService.getApproverTasks(pageable);
            model.addAttribute("summary", loanService.getDashboardSummary());
        }else{
             loanPage = loanService.getOperatorTasks(pageable);
        }


        model.addAttribute("loans", loanPage.getContent());
        model.addAttribute("currentPage", loanPage.getNumber());
        model.addAttribute("totalPages", loanPage.getTotalPages());
        model.addAttribute("totalItems", loanPage.getTotalElements());
        model.addAttribute("userRole", auth.getAuthorities().toString());


        return "loan-list";
    }
    

    @GetMapping("/loans/tambah")
    public String formTambahPengajuan(Model model) {
        model.addAttribute("applicationForm", new ApplicationFormRequestDTO());
        model.addAttribute("tenorOptions", TENOR_OPTIONS);
        return "loan-application-form";
    }

    @PostMapping("/loans/simpan-pengajuan")
    public String simpanPengajuan(
        @ModelAttribute("applicationForm") ApplicationFormRequestDTO dto,
        BindingResult bindingResult,
        @RequestParam String mode,
        Model model) {
            
        Set<ConstraintViolation<ApplicationFormRequestDTO>> violations;
        
        if(mode.equals("submit")) {
            violations = validator.validate(dto, OnSubmit.class);
        }else{
            violations = validator.validate(dto, OnDraft.class);
        }
        if(bindingResult.hasErrors() || !violations.isEmpty()){
            model.addAttribute("violations", violations);
            model.addAttribute("applicationForm", dto);
            model.addAttribute("tenorOptions", TENOR_OPTIONS);
            return "loan-application-form";
        }


        if(mode.equals("submit")){
            loanService.submitApplication(dto);
        }else{
            loanService.saveDraft(dto);
        }

        return "redirect:/applications/dashboard";

        
    }

    @GetMapping("/loans/edit/{id}")
    public String formEditDraft(@PathVariable("id") Integer id, Model model) {
        ApplicationFormRequestDTO dto = loanService.getLoanForEdit(id);

        model.addAttribute("applicationForm", dto);
        model.addAttribute("tenorOptions", TENOR_OPTIONS);
        return "loan-application-form";
    }
    
    
    @GetMapping("/loans/hapus/{id}")
    public String hapusLoan(@PathVariable("id") Integer id) {
        loanService.deleteLoanById(id);
        return "redirect:/applications/dashboard";
    }
    
    @GetMapping("/loans/detail/{id}")
    public String detailLoan(@PathVariable("id") Integer id, 
    @RequestParam (name = "scored", defaultValue = "false") boolean scored, 
    Model model) {

        LoanResponseDTO loanDTO = loanService.getLoanDetailForCurUser(id);

        model.addAttribute("loan", loanDTO);
        model.addAttribute("isAuthorized", loanDTO.isAuthorizedToApprove());

        if(scored){
            model.addAttribute("assessment", loanService.getAssessment(id));
        }

        return "loan-detail";
    }

    @PostMapping("/loans/keputusan")
    public String prosesKeputusan(
        @RequestParam("loanId") Integer loanId,
        @RequestParam("action") String action,
        @RequestParam("notes") String notes,
        RedirectAttributes redirectAttributes
    ) {
        try{
            loanService.submitApprovalDecision(loanId, action, notes);
        } catch (AccessDeniedException | IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("decisionError", e.getMessage());
            return "redirect:/loans/detail/" + loanId + "?scored=true";
        }
        loanService.submitApprovalDecision(loanId, action, notes);

        return "redirect:/applications/dashboard";
    }
    
}
