package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Debtor;
import com.example.demo.model.Loan;
import com.example.demo.model.LoanApproval;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.DebtorRepository;
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

    @Autowired 
    private DebtorRepository debtorRepository;

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

    private String generateApplicationNumber(){

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        long countToday = loanRepository.count() + 1;

        String sequencePart = String.format("%05d", countToday );

        return "APP-" + datePart + "-" + sequencePart;
    }

    private Debtor upsertDebtor(ApplicationFormRequestDTO dto, Debtor previousDebtor) {
        Debtor debtor;

        if(dto.getNik() != null && !dto.getNik().isBlank()){
            Debtor existingDebtor = debtorRepository.findByNik(dto.getNik());
            debtor = (existingDebtor != null) ? existingDebtor : new Debtor();         
        }else if(previousDebtor != null){
            debtor = previousDebtor;
        }else {
            debtor = new Debtor();
        }
      
        debtor.setNik(dto.getNik());
        debtor.setFullName(dto.getFullName());
        debtor.setEmail(dto.getEmail());
        debtor.setPhone(dto.getPhone());
        debtor.setBirthDate(dto.getBirthDate());
        debtor.setGender(dto.getGender());
        debtor.setAddress(dto.getAddress());

        return debtorRepository.save(debtor);


    }

    private Loan mapDtoToLoan(ApplicationFormRequestDTO dto, ApplicationStatus status){


        Loan loan = (dto.getLoanId() != null)
                    ? loanRepository.findById(dto.getLoanId()). orElse(new Loan())
                    : new Loan();

        Debtor previousDebtor = loan.getDebtor();
        Debtor debtor = upsertDebtor(dto, previousDebtor);

        loan.setDebtor(debtor);
        loan.setCompanyName(dto.getCompanyName());
        loan.setEmploymentType(dto.getEmploymentType());
        loan.setWorkDurationMonths(dto.getWorkDurationMonths());
        loan.setMonthlyIncome(dto.getMonthlyIncome());
        loan.setExistingInstallments(dto.getExistingInstallments());
        loan.setProductType(dto.getProductType());
        loan.setRequestedAmount(dto.getRequestedAmount());
        loan.setTenorMonths(dto.getTenorMonths());
        loan.setInterestScheme(dto.getInterestScheme());
        loan.setLoanPurpose(dto.getLoanPurpose());
        loan.setHasCollateral(dto.getHasCollateral());
        loan.setCollateralType(dto.getCollateralType());
        loan.setCollateralValue(dto.getCollateralValue());
        loan.setStatus(status);

        if(loan.getApplicationNumber() == null){
            loan.setApplicationNumber(generateApplicationNumber());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userRepository.findByUdomain(auth.getName());

        if(currUser != null){
            loan.setCreatedBy(currUser.getId());
        }


        return loan;
        
    }

    @Transactional 
    public Loan saveDraft(ApplicationFormRequestDTO dto){

        Loan loan = mapDtoToLoan(dto, ApplicationStatus.DRAFT);

     
        return loanRepository.save(loan);
        
    }

    @Transactional 
    public Loan submitApplication(ApplicationFormRequestDTO dto){

        Loan loan = mapDtoToLoan(dto, ApplicationStatus.SUBMITTED);
        return loanRepository.save(loan);

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
