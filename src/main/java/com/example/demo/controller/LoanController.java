package com.example.demo.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/loans")
    public String listLoans(Model model, HttpSession session) {
        List<LoanResponseDTO> loanResponseDTOs = loanService.getAllLoans();
        model.addAttribute("loans", loanResponseDTOs);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("userRole", auth.getAuthorities().toString());

        return "loan-list";
    }

    @GetMapping("/applications/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isApprover = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_APPROVER"));


        if(isApprover){
            model.addAttribute("loans", loanService.getApproverTasks());
            model.addAttribute("summary", loanService.getDashboardSummary());
        }else{
            model.addAttribute("loans", loanService.getOperatorTasks());
        }


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
        return "redirect:/loans";
    }
    
    @GetMapping("/loans/detail/{id}")
    public String detailLoan(@PathVariable("id") Integer id, Model model,  HttpSession session) {
        LoanResponseDTO loanDTO = loanService.getLoanDetailForCurUser(id);

        model.addAttribute("loan", loanDTO);
        model.addAttribute("isAuthorized", loanDTO.isAuthorizedToApprove());

        return "loan-detail";
    }

    @PostMapping("/loans/keputusan")
    public String prosesKeputusan(
        @RequestParam("loanId") Integer loanId,
        // @RequestParam("userId") Integer userId,
        @RequestParam("action") String action,
        @RequestParam("notes") String notes
    ) {
        loanService.submitApprovalDecision(loanId, action, notes);

        return "redirect:/loans";
    }
    
}
