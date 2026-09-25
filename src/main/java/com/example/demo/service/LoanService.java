package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanApproval;
import com.example.demo.model.Role;
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

            return new LoanResponseDTO(loan.getId(), loan.getDebtor().getFullName(), loan.getRequestedAmount(), loan.getStatus().name(), false);
        }).collect(Collectors.toList());
    }

    public LoanResponseDTO getLoanDetailForCurUser(Integer id){
        Loan loan = loanRepository.findById(id).orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String currUsername = auth.getName();

        User userInDb = userRepository.findByUdomain(currUsername);
        
        Long maxUserApprovalLimit = 0L;
        boolean isApprover = false;

        if(userInDb != null){
            
            for (Role role : userInDb.getRoles()){
                if(role.getRoleName().startsWith("ROLE_APPROVER")) {
                    isApprover = true;

                    if(role.getApprovalLimit() > maxUserApprovalLimit){
                        maxUserApprovalLimit = role.getApprovalLimit();
                    }
                }
            }
        }


        boolean bolehProses = isApprover && (loan.getRequestedAmount()
                                            .compareTo(BigDecimal.valueOf(maxUserApprovalLimit)) <= 0);


        return new LoanResponseDTO(loan.getId(), loan.getDebtor().getFullName(),loan.getRequestedAmount(), loan.getStatus().name(), bolehProses);
        
    }

    public void createNewLoan(Loan loan){
        loan.setStatus(ApplicationStatus.DRAFT);
        loan.setCreatedBy(1);
        loanRepository.save(loan);
    }

    public void deleteLoanById(Integer id){
        loanRepository.deleteById(id);
    }

    @Transactional
    public void submitApprovalDecision(Integer loanId, String action, String notes){
        Loan loan = loanRepository.findById(loanId).orElseThrow();

        loan.setStatus(ApplicationStatus.valueOf(action));
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
