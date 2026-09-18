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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            .authorizeHttpRequests(auth -> auth
                
            .requestMatchers("/login", "/style.css", "/h2-console/**").permitAll()

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
