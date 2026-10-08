package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ApplicationFormRequestDTO;
import com.example.demo.dto.DashboardSummaryDTO;
import com.example.demo.dto.LoanResponseDTO;
import com.example.demo.dto.UnderwritingAssessmentDTO;
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

@Service
@RequiredArgsConstructor
public class LoanService {
    
    private final LoanRepository loanRepository;
    private final LoanApprovalRepository loanApprovalRepository;
    private final UserRepository userRepository;
    private final DebtorRepository debtorRepository;

    

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


    public Page<LoanResponseDTO> getOperatorTasks(Pageable pageable){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userRepository.findByUdomain(auth.getName());

        if(currUser == null){
            return Page.empty(pageable);
        }

        List<ApplicationStatus> operatorStatus = Arrays.asList(ApplicationStatus.DRAFT, ApplicationStatus.REJECTED);
        Page<Loan> loans = loanRepository.findByCreatedByAndStatusIn(currUser.getId(), operatorStatus, pageable);

        return loans.map(loan -> toLoanResponseDTO(loan, false));
    }

    public Page<LoanResponseDTO> getApproverTasks(Pageable pageable){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = userRepository.findByUdomain(auth.getName());

        if(currUser == null){
            return Page.empty(pageable);
        }

        BigDecimal maxUserApprovalLimit = getMaxApprovalLimit(currUser);

        Page<Loan> loans = loanRepository.findApproverEligibleTasks(ApplicationStatus.SUBMITTED, maxUserApprovalLimit, pageable);

        return loans.map(loan -> toLoanResponseDTO(loan, true));

    }

    private BigDecimal calculateAnew(Loan loan){
        BigDecimal requested = loan.getRequestedAmount();
        Integer tenor = loan.getTenorMonths();

        if (requested == null || tenor == null || tenor == 0){
            return BigDecimal.ZERO;
        }

        BigDecimal pokokPerBulan = requested.divide(new BigDecimal(tenor), 2, RoundingMode.HALF_UP);

        BigDecimal bungaPerBulan = requested.multiply(new BigDecimal("0.01"));

        return pokokPerBulan.add(bungaPerBulan);
    }

    private BigDecimal calculateDSR(Loan loan){
        BigDecimal monthlyIncome = loan.getMonthlyIncome();

        if(monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }

        BigDecimal existing = (loan.getExistingInstallments() != null)
                ? loan.getExistingInstallments() : BigDecimal.ZERO;

        BigDecimal anew = calculateAnew(loan);

        BigDecimal totalCicilan = existing.add(anew);

        return totalCicilan.divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

    }

    private BigDecimal calculateLTV(Loan loan){
        if(!Boolean.TRUE.equals(loan.getHasCollateral())){
            return null;
        }

        BigDecimal collateralValue = loan.getCollateralValue();
        BigDecimal requested = loan.getRequestedAmount();

        if(collateralValue == null || collateralValue.compareTo(BigDecimal.ZERO) == 0){
            return null;
        }

        return requested.divide(collateralValue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));

    }

    private String determineRecommendation(BigDecimal dsr, BigDecimal ltv){
        int dsrRisk;

        if(dsr.compareTo(new BigDecimal("40")) <= 0){
            dsrRisk = 1;
        } else if(dsr.compareTo(new BigDecimal("50")) <= 0){
            dsrRisk = 2;
        } else{
            dsrRisk = 3;
        }

        int ltvRisk;

        if(ltv == null || ltv.compareTo(new BigDecimal("80")) <= 0){
            ltvRisk = 1;
        }else if(ltv.compareTo(new BigDecimal("90")) <= 0){
            ltvRisk = 2;
        }else{
            ltvRisk = 3;
        }

        int finalRisk = Math.max(dsrRisk, ltvRisk);

        if(finalRisk == 1){
            return "FAST_PATH_APPROVE";
        }else if(finalRisk == 2){
            return "MANUAL_REVIEW";
        }else{
            return "SYSTEM_REJECT";
        }

    }

    public UnderwritingAssessmentDTO getAssessment(Integer loanId){
        Loan loan = loanRepository.findById(loanId).orElseThrow();

        BigDecimal anew = calculateAnew(loan);
        BigDecimal dsr = calculateDSR(loan);
        BigDecimal ltv = calculateLTV(loan);
        String recommendation = determineRecommendation(dsr, ltv);

        return new UnderwritingAssessmentDTO(anew, dsr, ltv, recommendation);
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

        if (loan.getStatus() != ApplicationStatus.DRAFT
            && loan.getStatus() != ApplicationStatus.REJECTED){
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUserInDb = userRepository.findByUdomain(auth.getName());

        if("APPROVED".equals(action)){
            BigDecimal limit = getMaxApprovalLimit(currUserInDb);
            BigDecimal amount = loan.getRequestedAmount();


            if(amount != null && limit.compareTo(amount) < 0){
                throw new AccessDeniedException(
                    "Limit Approval Anda kurang untuk menyetujui plafon Rp " + amount.toPlainString()
                );
            }
        }

        if("REJECTED".equals(action)){
            if(notes == null || notes.isBlank()){
                throw new IllegalArgumentException("Alasan penolakan wajib");
            }
        }

        loan.setStatus(ApplicationStatus.valueOf(action));
        loanRepository.saveAndFlush(loan);

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
