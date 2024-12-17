package it.lessons.ticketplatform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(); // Usa il tuo handler personalizzato
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disabilitiamo temporaneamente il CSRF per evitare errori
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/resources/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/support/admin/**").hasRole("ADMIN")
                .requestMatchers("/support/operator/**").hasRole("OPERATOR")
                .requestMatchers("/support/tickets/**").hasAnyRole("ADMIN", "OPERATOR") // Permetti l'accesso ai ticket
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }
}