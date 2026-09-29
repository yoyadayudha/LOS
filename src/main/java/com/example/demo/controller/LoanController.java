package com.example.demo.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanApproval;
import com.example.demo.model.User;
import com.example.demo.repository.LoanApprovalRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoanService;
import com.example.demo.validation.OnDraft;
import com.example.demo.validation.OnSubmit;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class LoanController {
    //lombok
    //dependency injector @Autowired
    
    @Autowired
    private Validator validator;

    @Autowired
    private LoanService loanService;

    @GetMapping("/loans")
    public String listLoans(Model model, HttpSession session) {
        List<LoanResponseDTO> loanResponseDTOs = loanService.getAllLoans();
        model.addAttribute("loans", loanResponseDTOs);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("userRole", auth.getAuthorities().toString());

        return "loan-list";
    }

    @GetMapping("/loans/tambah")
    public String formTambahPengajuan(Model model) {
        model.addAttribute("applicationForm", new ApplicationFormRequestDTO());
        return "loan-application-form";
    }

    @PostMapping("/loans/simpan-pengajuan")
    public String simpanPengajuan(
        @ModelAttribute("applicationForm") ApplicationFormRequestDTO dto,
        @RequestParam String mode,
        Model model) {
        //TODO: process POST request
        Set<ConstraintViolation<ApplicationFormRequestDTO>> violations;
        

        if(mode.equals("submit")) {
            violations = validator.validate(dto, OnSubmit.class);
        }else{
            violations = validator.validate(dto, OnDraft.class);
        }


        if(!violations.isEmpty()) {
            model.addAttribute("violations", violations);
            model.addAttribute("applicationForm", dto);
            return "loan-application-form";
        }

        if(mode.equals("submit")){
            loanService.submitApplication(dto);
        }else{
            loanService.saveDraft(dto);
        }

        return "redirect:/loans";

        
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
