package pl.studenci.systemoceniania.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(auth -> auth
                        // Zasoby publiczne
                        .requestMatchers("/css/**", "/js/**", "/login", "/public/**", "/api/**").permitAll()

                        // Plan zajęć – pracownik i admin
                        .requestMatchers("/pracownik/plany/**").hasAnyRole("ADMIN", "PRACOWNIK")

                        // 🔥 TYLKO ADMIN MOŻE ZARZĄDZAĆ PRACOWNIKAMI
                        .requestMatchers("/pracownicy/**").hasRole("ADMIN")

                        // Pozostałe – pracownik i admin
                        .requestMatchers("/sale/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/przedmioty/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/oceny/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/kierunki/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/student/**").hasRole("STUDENT")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

            if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_PRACOWNIK")) {
                response.sendRedirect("/pracownik/dashboard");
            } else if (roles.contains("ROLE_STUDENT")) {
                response.sendRedirect("/student/dashboard");
            } else {
                response.sendRedirect("/login");
            }
        };
    }
}