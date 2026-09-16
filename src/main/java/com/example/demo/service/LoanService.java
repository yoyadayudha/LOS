package com.example.demo.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanApproval;
import com.example.demo.model.User;
import com.example.demo.repository.LoanApprovalRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service 
public class LoanService {
    
    @Autowired
    private LoanRepository loanRepository;

    @Autowired 
    private LoanApprovalRepository loanApprovalRepository;

    @Autowired
    private UserRepository userRepository;

    public List<LoanResponseDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();

        return loans.stream().map(loan -> {

            return new LoanResponseDTO(loan.getId(), loan.getDebiturName(), loan.getAmount(), loan.getStatus(), false);
        }).collect(Collectors.toList());
    }

    public LoanResponseDTO getLoanDetailForCurUser(Integer id){
        Loan loan = loanRepository.findById(id).orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String currUsername = auth.getName();

        User userInDb = userRepository.findByUdomain(currUsername);
        
        Long userApprovalLimit = 0L;

        if(userInDb != null){
            userApprovalLimit = userInDb.getApprovalLimit();
        }

        if(currUsername.equals("approver1")){
            userApprovalLimit = 50000000L;
        }else if(currUsername.equals("approver2")){
            userApprovalLimit = 250000000L;
        }else if(currUsername.equals("approver3")){
            userApprovalLimit = 1000000000L;
        }

        boolean bolehProses = loan.getAmount() <= userApprovalLimit;

        return new LoanResponseDTO(loan.getId(), loan.getDebiturName(), loan.getAmount(), loan.getStatus(), bolehProses);
        
    }

    public void createNewLoan(Loan loan){
        loan.setStatus("PENDING");
        loan.setCreatedBy(1);
        loanRepository.save(loan);
    }

    public void deleteLoanById(Integer id){
        loanRepository.deleteById(id);
    }

    @Transactional
    public void submitApprovalDecision(Integer loanId, String action, String notes){
        Loan loan = loanRepository.findById(loanId).orElseThrow();

        loan.setStatus(action);
        loanRepository.saveAndFlush(loan);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUserInDb = userRepository.findByUdomain(auth.getName());

        LoanApproval logBaru = new LoanApproval();
        logBaru.setLoan(loan);
        logBaru.setAction(action);
        logBaru.setNotes(notes);

        if(currUserInDb != null){
            logBaru.setUser(currUserInDb);
        }


        loanApprovalRepository.saveAndFlush(logBaru);
    }

}
