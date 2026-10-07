package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.dto.DashboardSummaryDTO;
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

import lombok.RequiredArgsConstructor;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {
    
    private final LoanRepository loanRepository;
    private final LoanApprovalRepository loanApprovalRepository;
    private final UserRepository userRepository;
    private final DebtorRepository debtorRepository;


    public List<LoanResponseDTO> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();

        return loans.stream().map(loan -> toLoanResponseDTO(loan, false)).collect(Collectors.toList());
    }
    

    public LoanResponseDTO getLoanDetailForCurUser(Integer id){
        Loan loan = loanRepository.findById(id).orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String currUsername = auth.getName();

        User userInDb = userRepository.findByUdomain(currUsername);
        
        BigDecimal maxUserApprovalLimit = getMaxApprovalLimit(userInDb);

        boolean bolehProses = maxUserApprovalLimit.compareTo(BigDecimal.ZERO) > 0
                              && loan.getRequestedAmount() != null
                              && loan.getRequestedAmount().compareTo(maxUserApprovalLimit) <= 0;


        return toLoanResponseDTO(loan, bolehProses);
        
    }


    public List<LoanResponseDTO> getOperatorTasks(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userRepository.findByUdomain(auth.getName());

        if(currUser == null){
            return List.of();
        }

        List<ApplicationStatus> operatorStatus = Arrays.asList(ApplicationStatus.DRAFT, ApplicationStatus.REJECTED);
        List<Loan> loans = loanRepository.findByCreatedByAndStatusIn(currUser.getId(), operatorStatus);

        return loans.stream()
            .map(loan -> toLoanResponseDTO(loan, false))

            .collect(Collectors.toList());
    }

    public List<LoanResponseDTO> getApproverTasks(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userRepository.findByUdomain(auth.getName());

        if(currUser == null){
            return List.of();
        }

        BigDecimal maxUserApprovalLimit = getMaxApprovalLimit(currUser);

        List<Loan> loans = loanRepository.findApproverEligibleTasks(ApplicationStatus.SUBMITTED, maxUserApprovalLimit);

        return loans.stream()
        .map(loan -> toLoanResponseDTO(loan, true))
            .collect(Collectors.toList());


    }


    public DashboardSummaryDTO getDashboardSummary(){
        long totalSubmitted = loanRepository.countByStatus(ApplicationStatus.SUBMITTED);
        long totalInReview = loanRepository.countByStatus(ApplicationStatus.IN_REVIEW);
        long totalApproved = loanRepository.countByStatus(ApplicationStatus.APPROVED);
        long totalRejected = loanRepository.countByStatus(ApplicationStatus.REJECTED);


        return new DashboardSummaryDTO(totalSubmitted, totalInReview, totalApproved, totalRejected);
    }


    private BigDecimal getMaxApprovalLimit(User user){
        BigDecimal maxLimit = BigDecimal.ZERO;


        if(user == null || user.getRoles() == null){
            return maxLimit;
        }


        for(Role role : user.getRoles()){
            if(role != null
                && role.getRoleName() != null
                && role.getRoleName().startsWith("ROLE_APPROVER")
                && role.getApprovalLimit() != null
                && role.getApprovalLimit().compareTo(maxLimit) > 0
            ){
            maxLimit = role.getApprovalLimit();

            }

        }


        return maxLimit;
    }


    private LoanResponseDTO toLoanResponseDTO(Loan loan, boolean authorizedToApprove){
        String borrowerName = (loan.getDebtor() != null) ? loan.getDebtor().getFullName() : null;
        String statusName = (loan.getStatus() != null) ? loan.getStatus().name() : null;

        return new LoanResponseDTO(
            loan.getId(), loan.getApplicationNumber(), borrowerName, loan.getRequestedAmount(), statusName, authorizedToApprove);
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

    private ApplicationFormRequestDTO mapLoanToDto(Loan loan){
        ApplicationFormRequestDTO dto = new ApplicationFormRequestDTO();

        dto.setLoanId(loan.getId());

        Debtor debtor = loan.getDebtor();

        if(debtor != null){
            dto.setNik(debtor.getNik());
            dto.setFullName(debtor.getFullName());
            dto.setEmail(debtor.getEmail());
            dto.setPhone(debtor.getPhone());
            dto.setBirthDate(debtor.getBirthDate());
            dto.setGender(debtor.getGender());
            dto.setAddress(debtor.getAddress());
        }

        dto.setCompanyName(loan.getCompanyName());
        dto.setEmploymentType(loan.getEmploymentType());
        dto.setWorkDurationMonths(loan.getWorkDurationMonths());
        dto.setMonthlyIncome(loan.getMonthlyIncome());
        dto.setExistingInstallments(loan.getExistingInstallments());
        dto.setProductType(loan.getProductType());
        dto.setRequestedAmount(loan.getRequestedAmount());
        dto.setTenorMonths(loan.getTenorMonths());
        dto.setInterestScheme(loan.getInterestScheme());
        dto.setLoanPurpose(loan.getLoanPurpose());
        dto.setHasCollateral(loan.getHasCollateral());
        dto.setCollateralType(loan.getCollateralType());
        dto.setCollateralValue(loan.getCollateralValue());

        return dto;
    }

    public ApplicationFormRequestDTO getLoanForEdit(Integer id){
        Loan loan = loanRepository.findById(id).orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userRepository.findByUdomain(auth.getName());

        if(currUser == null || loan.getCreatedBy() == null
            || !loan.getCreatedBy().equals(currUser.getId())){
                throw new AccessDeniedException("Bukan pemilik draft ini");
            }

        if (loan.getStatus() != ApplicationStatus.DRAFT){
            throw new IllegalStateException("Hanya draf yang bisa diedit");
        }

        return mapLoanToDto(loan);
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
