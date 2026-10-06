package com.example.demo;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.DebtorRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import java.time.LocalDate;
import com.example.demo.model.Debtor;
import com.example.demo.model.Loan;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.model.Gender;
import com.example.demo.model.ProductType;
import com.example.demo.model.InterestScheme;
import com.example.demo.model.EmploymentType;
import com.example.demo.model.CollateralType;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
public class DemoApplication {

    public static void main(String[] eloquence) {
        SpringApplication.run(DemoApplication.class, eloquence);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

    @Bean
    public CommandLineRunner dataAwal(
            UserRepository userRepository,
            LoanRepository loanRepository,
            RoleRepository roleRepository,
            DebtorRepository debtorRepository) {
        return args -> {
        
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String securePassword = encoder.encode("password123");

            Role roleOperator = null;
            Role roleAnalyst = null;
            Role roleApprover1 = null;
            Role roleApprover2 = null;
            Role roleApprover3 = null;
            Role roleInquiry = null;

            if (roleRepository.count() == 0) {
                roleOperator = new Role();
                roleOperator.setId(1);
                roleOperator.setRoleName("ROLE_OPERATOR");
                roleOperator.setDescription("Input Data Pengajuan Kredit");
                roleOperator.setApprovalLimit(new BigDecimal("0")); 
                roleRepository.save(roleOperator);

                roleAnalyst = new Role();
                roleAnalyst.setId(2);
                roleAnalyst.setRoleName("ROLE_ANALYST");
                roleAnalyst.setDescription("Review Underwriting Kredit");
                roleAnalyst.setApprovalLimit(new BigDecimal("0"));
                roleRepository.save(roleAnalyst);

                roleApprover1 = new Role();
                roleApprover1.setId(3);
                roleApprover1.setRoleName("ROLE_APPROVER_L1");
                roleApprover1.setDescription("Approval Plafon Tingkat SPV Level 1");
                roleApprover1.setApprovalLimit(new BigDecimal("50000000"));
                roleRepository.save(roleApprover1);

                roleApprover2 = new Role();
                roleApprover2.setId(4);
                roleApprover2.setRoleName("ROLE_APPROVER_L2");
                roleApprover2.setDescription("Approval Plafon Tingkat SPV Level 2");
                roleApprover2.setApprovalLimit(new BigDecimal("250000000"));
                roleRepository.save(roleApprover2);

                roleApprover3 = new Role();
                roleApprover3.setId(5);
                roleApprover3.setRoleName("ROLE_APPROVER_L3");
                roleApprover3.setDescription("Approval Plafon Tingkat SPV Level 3");
                roleApprover3.setApprovalLimit(new BigDecimal("1000000000"));
                roleRepository.save(roleApprover3);

                roleInquiry = new Role();
                roleInquiry.setId(6);
                roleInquiry.setRoleName("ROLE_INQUIRY");
                roleInquiry.setDescription("Murni Mengintip & Monitoring Data");
                roleInquiry.setApprovalLimit(new BigDecimal("0"));
                roleRepository.save(roleInquiry);
            } else {
                roleOperator = roleRepository.findByRoleName("ROLE_OPERATOR");
                roleApprover1 = roleRepository.findByRoleName("ROLE_APPROVER_L1");
                roleInquiry = roleRepository.findByRoleName("ROLE_INQUIRY");
            }

            if (userRepository.count() == 0) {
                User op = new User();
                op.setId(10);
                op.setUdomain("U561012");
                op.setPassword(securePassword);
                op.setRoles(Arrays.asList(roleOperator));
                userRepository.save(op);

                User op1 = new User();
                op1.setId(36);
                op1.setUdomain("U111111");
                op1.setPassword(securePassword);
                op1.setRoles(Arrays.asList(roleOperator));
                userRepository.save(op1);

                User app2 = new User();
                app2.setId(12);
                app2.setUdomain("U222222");
                app2.setPassword(securePassword);
                app2.setRoles(Arrays.asList(roleRepository.findById(4).orElseThrow())); 
                userRepository.save(app2);

                User doubleAgent = new User();
                doubleAgent.setId(99);
                doubleAgent.setUdomain("U999999");
                doubleAgent.setPassword(securePassword);
                doubleAgent.setRoles(Arrays.asList(roleOperator, roleInquiry));
                userRepository.save(doubleAgent);
            }

            if (loanRepository.count() == 0) {

                Debtor hitagi = new Debtor();
                hitagi.setNik("3174010101900001");
                hitagi.setFullName("Hitagi Senjougahara");
                hitagi.setEmail("hitagi@mail.com");
                hitagi.setPhone("081234567890");
                hitagi.setBirthDate(LocalDate.of(1995, 7, 7));
                hitagi.setGender(Gender.FEMALE);
                hitagi.setAddress("Naoetsu, Prefektur Nagano, Jepang");
                debtorRepository.save(hitagi);

                Debtor araragi = new Debtor();
                araragi.setNik("3174010101900002");
                araragi.setFullName("Koyomi Araragi");
                araragi.setEmail("araragi@mail.com");
                araragi.setPhone("089876543210");
                araragi.setBirthDate(LocalDate.of(1993, 4, 7));
                araragi.setGender(Gender.MALE);
                araragi.setAddress("Naoetsu, Prefektur Nagano, Jepang");
                debtorRepository.save(araragi);

                Debtor hanekawa = new Debtor();
                hanekawa.setNik("3174010101900003");
                hanekawa.setFullName("Tsubasa Hanekawa");
                hanekawa.setEmail("hanekawa@mail.com");
                hanekawa.setPhone("081111222333");
                hanekawa.setBirthDate(LocalDate.of(1994, 4, 10));
                hanekawa.setGender(Gender.FEMALE);
                hanekawa.setAddress("Naoetsu, Prefektur Nagano, Jepang");
                debtorRepository.save(hanekawa);

                Loan l1 = new Loan();
                l1.setApplicationNumber("APP-20261001-00001");
                l1.setDebtor(hitagi);
                l1.setCompanyName("PT Oddity Abadi");
                l1.setEmploymentType(EmploymentType.PERMANENT);
                l1.setWorkDurationMonths(24);
                l1.setMonthlyIncome(new BigDecimal("15000000"));
                l1.setExistingInstallments(new BigDecimal("0"));
                l1.setProductType(ProductType.KPR);
                l1.setRequestedAmount(new BigDecimal("40000000"));
                l1.setTenorMonths(60);
                l1.setInterestScheme(InterestScheme.ANNUITY);
                l1.setLoanPurpose("Renovasi rumah");
                l1.setHasCollateral(true);
                l1.setCollateralType(CollateralType.PROPERTY);
                l1.setCollateralValue(new BigDecimal("80000000"));
                l1.setStatus(ApplicationStatus.DRAFT);
                l1.setCreatedBy(10);
                loanRepository.save(l1);

                Loan l2 = new Loan();
                l2.setApplicationNumber("APP-20261001-00002");
                l2.setDebtor(araragi);
                l2.setCompanyName("PT Vampire Holdings");
                l2.setEmploymentType(EmploymentType.PERMANENT);
                l2.setWorkDurationMonths(36);
                l2.setMonthlyIncome(new BigDecimal("20000000"));
                l2.setExistingInstallments(new BigDecimal("0"));
                l2.setProductType(ProductType.KKB);
                l2.setRequestedAmount(new BigDecimal("100000000"));
                l2.setTenorMonths(36);
                l2.setInterestScheme(InterestScheme.FLAT);
                l2.setLoanPurpose("Pembelian kendaraan");
                l2.setHasCollateral(true);
                l2.setCollateralType(CollateralType.VEHICLE);
                l2.setCollateralValue(new BigDecimal("120000000"));
                l2.setStatus(ApplicationStatus.SUBMITTED);
                l2.setCreatedBy(10);
                loanRepository.save(l2);

                Loan l3 = new Loan();
                l3.setApplicationNumber("APP-20261001-00003");
                l3.setDebtor(hanekawa);
                l3.setCompanyName("PT Cat Nekomata");
                l3.setEmploymentType(EmploymentType.PERMANENT);
                l3.setWorkDurationMonths(48);
                l3.setMonthlyIncome(new BigDecimal("40000000"));
                l3.setExistingInstallments(new BigDecimal("0"));
                l3.setProductType(ProductType.KPR);
                l3.setRequestedAmount(new BigDecimal("500000000"));
                l3.setTenorMonths(120);
                l3.setInterestScheme(InterestScheme.ANNUITY);
                l3.setLoanPurpose("Pembelian properti");
                l3.setHasCollateral(true);
                l3.setCollateralType(CollateralType.PROPERTY);
                l3.setCollateralValue(new BigDecimal("700000000"));
                l3.setStatus(ApplicationStatus.SUBMITTED);
                l3.setCreatedBy(10);
                loanRepository.save(l3);

                Loan l4 = new Loan();
                l4.setApplicationNumber("APP-20261001-00004");
                l4.setDebtor(hitagi);
                l4.setProductType(ProductType.KKB);
                l4.setRequestedAmount(new BigDecimal("80000000"));
                l4.setTenorMonths(24);
                l4.setInterestScheme(InterestScheme.FLAT);
                l4.setHasCollateral(true);
                l4.setCollateralType(CollateralType.VEHICLE);
                l4.setCollateralValue(new BigDecimal("100000000"));
                l4.setStatus(ApplicationStatus.APPROVED);
                l4.setCreatedBy(10);
                loanRepository.save(l4);

                Loan l5 = new Loan();
                l5.setApplicationNumber("APP-20261001-00005");
                l5.setDebtor(araragi);
                l5.setProductType(ProductType.KPR);
                l5.setRequestedAmount(new BigDecimal("300000000"));
                l5.setTenorMonths(60);
                l5.setInterestScheme(InterestScheme.ANNUITY);
                l5.setHasCollateral(true);
                l5.setCollateralType(CollateralType.PROPERTY);
                l5.setCollateralValue(new BigDecimal("400000000"));
                l5.setStatus(ApplicationStatus.REJECTED);
                l5.setCreatedBy(10);
                loanRepository.save(l5);
            }

        };
    }
}
