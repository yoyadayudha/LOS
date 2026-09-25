package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.example.demo.model.Loan;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.Debtor;
import com.example.demo.model.Gender;
import com.example.demo.model.ApplicationStatus;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.DebtorRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

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
                roleOperator.setApprovalLimit(0L); 
                roleRepository.save(roleOperator);

                roleAnalyst = new Role();
                roleAnalyst.setId(2);
                roleAnalyst.setRoleName("ROLE_ANALYST");
                roleAnalyst.setDescription("Review Underwriting Kredit");
                roleAnalyst.setApprovalLimit(0L);
                roleRepository.save(roleAnalyst);

                roleApprover1 = new Role();
                roleApprover1.setId(3);
                roleApprover1.setRoleName("ROLE_APPROVER_L1");
                roleApprover1.setDescription("Approval Plafon Tingkat SPV Level 1");
                roleApprover1.setApprovalLimit(50000000L);
                roleRepository.save(roleApprover1);

                roleApprover2 = new Role();
                roleApprover2.setId(4);
                roleApprover2.setRoleName("ROLE_APPROVER_L2");
                roleApprover2.setDescription("Approval Plafon Tingkat SPV Level 2");
                roleApprover2.setApprovalLimit(250000000L);
                roleRepository.save(roleApprover2);

                roleApprover3 = new Role();
                roleApprover3.setId(5);
                roleApprover3.setRoleName("ROLE_APPROVER_L3");
                roleApprover3.setDescription("Approval Plafon Tingkat SPV Level 3");
                roleApprover3.setApprovalLimit(1000000000L);
                roleRepository.save(roleApprover3);

                roleInquiry = new Role();
                roleInquiry.setId(6);
                roleInquiry.setRoleName("ROLE_INQUIRY");
                roleInquiry.setDescription("Murni Mengintip & Monitoring Data");
                roleInquiry.setApprovalLimit(0L);
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
                Debtor debtor1 = new Debtor();
                debtor1.setNik("3174010101900001");
                debtor1.setFullName("Haruhi Suzumiya");
                debtor1.setEmail("haruhi@mail.com");
                debtor1.setPhone("081234567890");
                debtor1.setBirthDate(LocalDate.of(1992, 10, 8));
                debtor1.setGender(Gender.FEMALE);
                debtor1.setAddress("Nishinomiya, Prefektur Hyogo 662-0082, Jepang");
                debtorRepository.save(debtor1);

                Loan l1 = new Loan();
                l1.setRequestedAmount(new BigDecimal("40000000"));
                l1.setStatus(ApplicationStatus.DRAFT);
                l1.setCreatedBy(10);
                l1.setDebtor(debtor1);
                loanRepository.save(l1);

                Debtor debtor2 = new Debtor();
                debtor2.setNik("3174010101900002");
                debtor2.setFullName("Hououin Kyouma");
                debtor2.setEmail("okarin@mail.com");
                debtor2.setPhone("089876543210");
                debtor2.setBirthDate(LocalDate.of(1991, 12, 14));
                debtor2.setGender(Gender.MALE);
                debtor2.setAddress(" 3-18-1 Soto-Kanda, Chiyoda-ku, Tokyo");
                debtorRepository.save(debtor2);

                Loan l2 = new Loan();
                l2.setRequestedAmount(new BigDecimal("75000000"));
                l2.setStatus(ApplicationStatus.DRAFT);
                l2.setCreatedBy(10);
                l2.setDebtor(debtor2);
                loanRepository.save(l2);
            }
        };
    }
}
