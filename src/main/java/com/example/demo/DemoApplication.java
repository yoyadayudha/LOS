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
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

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
            RoleRepository roleRepository) {
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
                Loan l1 = new Loan();
                l1.setDebiturName("Haruhi Suzumiya");
                l1.setAmount(40000000L);
                l1.setStatus("PENDING");
                l1.setCreatedBy(10);
                loanRepository.save(l1);

                Loan l2 = new Loan();
                l2.setDebiturName("Hououin Kyouma");
                l2.setAmount(75000000L);
                l2.setStatus("PENDING");
                l2.setCreatedBy(10);
                loanRepository.save(l2);
            }
        };
    }
}
