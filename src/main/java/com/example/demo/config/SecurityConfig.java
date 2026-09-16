package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration

@EnableWebSecurity 
public class SecurityConfig {
    
    private final HttpSessionEventPublisher httpSessionEventPublisher;

    SecurityConfig(HttpSessionEventPublisher httpSessionEventPublisher) {
        this.httpSessionEventPublisher = httpSessionEventPublisher;
    }

    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder){
        String hashedPwd = encoder.encode("password123");

        UserDetails operator = User.builder().username("operator1").password(hashedPwd).roles("OPERATOR").build();
        UserDetails analyst = User.builder().username("analis1").password(hashedPwd).roles("ANALYST").build();
        UserDetails approver1 = User.builder().username("approver1").password(hashedPwd).roles("APPROVER").build();
        UserDetails approver2 = User.builder().username("approver2").password(hashedPwd).roles("APPROVER").build();
        UserDetails approver3 = User.builder().username("approver3").password(hashedPwd).roles("APPROVER").build();

        return new InMemoryUserDetailsManager(operator, analyst, approver1, approver2, approver3);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                
            .requestMatchers("/login", "/style.css").permitAll()

            .requestMatchers("/applications/**").authenticated()
            
            .anyRequest().authenticated()
        )

        .formLogin(form -> form
            .loginPage("/login")

            .loginProcessingUrl("/login")

            .defaultSuccessUrl("/applications/dashboard", true)

            .failureUrl("/login?error+true")

            .permitAll()
        )

        .sessionManagement(session -> session
            .maximumSessions(1)

            .maxSessionsPreventsLogin(false)
        )

        .logout(logout -> logout
            .logoutUrl("/logout")
            
            .invalidateHttpSession(true)

            .clearAuthentication(true)

            .deleteCookies("JSESSIONID")

            .logoutSuccessUrl("/login?logout=true")

            .permitAll()
        );

        return http.build();
    }

    

}
