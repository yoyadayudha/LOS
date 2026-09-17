package com.example.demo;

import com.example.demo.repository.LoanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.example.demo.model.Loan;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo.repository") 
public class DemoApplication {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    DemoApplication(UserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public static void main(String[] eloquence) {
        SpringApplication.run(DemoApplication.class, eloquence);
    }

	@Bean 
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }

	@Bean
	public CommandLineRunner dataAwal(UserRepository userRepository){
		return args -> {
			if (userRepository.count() == 0){

				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

				String securePassword = encoder.encode("password123");

				//Operator
				User op = new User();
				op.setId(0);
				op.setUdomain("operator1");
				op.setPassword(securePassword);
				op.setRole("ROLE_OPERATOR");
				op.setApprovalLimit(0L);
				userRepository.save(op);

				//1. SPV lv 1 (Limit 50 Juta)
				User app1 = new User();
				app1.setId(1);
				app1.setUdomain("approver1");
				app1.setPassword(securePassword);
				app1.setRole("ROLE_APPROVER");
				app1.setApprovalLimit(50000000L);
				userRepository.save(app1);

				//2. SPV lv 2 (Limit 250 Juta)
				User app2 = new User();
				app2.setId(2);
				app2.setUdomain("approver2");
				app2.setPassword(securePassword);
				app2.setRole("ROLE_APPROVER");
				app2.setApprovalLimit(250000000L);
				userRepository.save(app2);

				//3. SPV lv 3 (Limit 1 M)
				User app3 = new User();
				app3.setId(3);
				app3.setUdomain("approver3");
				app3.setPassword(securePassword);
				app3.setRole("ROLE_APPROVER");
				app3.setApprovalLimit(1000000000L);
				userRepository.save(app3);

				if(loanRepository.count() == 0){
					Loan l1 = new Loan();

					l1.setDebiturName("Haruhi Suzumiya");
					l1.setAmount(40000000L);
					l1.setStatus("PENDING");
					l1.setCreatedBy(0);
					loanRepository.save(l1);

					Loan l2 = new Loan();

					l2.setDebiturName("Hououin Kyouma");
					l2.setAmount(75000000L);
					l2.setStatus("PENDING");
					l2.setCreatedBy(0);
					loanRepository.save(l2);
				}

			}
		};
	}
}
