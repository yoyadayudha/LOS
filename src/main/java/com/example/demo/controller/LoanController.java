package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.model.Loan;
import com.example.demo.model.LoanApproval;
import com.example.demo.model.User;
import com.example.demo.repository.LoanApprovalRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.transaction.annotation.Transactional;



@Controller 
public class LoanController {
    
    @Autowired 
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private LoanApprovalRepository loanApprovalRepository;

    @GetMapping("/loans")
    public String listLoans(Model model, HttpSession session) {
        List<Loan> daftarLoan = loanRepository.findAll();
        model.addAttribute("loans", daftarLoan);

        String usernameDariSession = (String) session.getAttribute("userAktif");

        User userLogin = userRepository.findByUdomain(usernameDariSession);

        if (userLogin != null){
            model.addAttribute("userRole", userLogin.getRole());
        }else{
            model.addAttribute("userRole", "ROLE_OPERATOR");
        }

        return "loan-list";
    }

    @GetMapping("/loans/tambah")
    public String formTambahLoan(Model model) {
        model.addAttribute("loanForm", new Loan());
        return "loan-form";
    }

    @PostMapping("/loans/simpan")
    public String simpanLoan(@ModelAttribute("loanForm") Loan loan) {
        //TODO: process POST request
        loan.setStatus("PENDING");
        loan.setCreatedBy(1);

        loanRepository.save(loan);
        return "redirect:/loans";
    }
    
    @GetMapping("/loans/hapus/{id}")
    public String hapusLoan(@PathVariable("id") Integer id) {
        loanRepository.deleteById(id);
        return "redirect:/loans";
    }
    
    @GetMapping("/loans/detail/{id}")
    public String detailLoan(@PathVariable("id") Integer id, Model model,  HttpSession session) {
        Loan loan = loanRepository.findById(id).orElseThrow();
        
        String usernameDariSession = (String) session.getAttribute("userAktif");
        User userLogin = userRepository.findByUdomain(usernameDariSession);
        
        if(userLogin == null){
            userLogin = new User();
            userLogin.setUdomain("Guest");
            userLogin.setApprovalLimit(0L);
        }

        boolean bolehProses = loan.getAmount() <= userLogin.getApprovalLimit();

        model.addAttribute("loan", loan);
        model.addAttribute("user", userLogin);
        model.addAttribute("isAuthorized", bolehProses);

        return "loan-detail";
    }

    @Transactional
    @PostMapping("/loans/keputusan")
    public String prosesKeputusan(
        @RequestParam("loanId") Integer loanId,
        @RequestParam("userId") Integer userId,
        @RequestParam("action") String action,
        @RequestParam("notes") String notes
    ) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        loan.setStatus(action);
        loanRepository.saveAndFlush(loan);

        LoanApproval logbaru = new LoanApproval();

        logbaru.setLoan(loan);
        logbaru.setUser(user);
        logbaru.setAction(action);
        logbaru.setNotes(notes);

        loanApprovalRepository.saveAndFlush(logbaru);

        return "redirect:/loans";
    }
    
}
