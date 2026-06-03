package pl.studenci.systemoceniania.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        UserDetails pracownik = User.builder()
                .username("nauczyciel")
                .password("nauczyciel123")
                .roles("PRACOWNIK")
                .build();

        UserDetails student = User.builder()
                .username("student")
                .password("student123")
                .roles("STUDENT")
                .build();

        return new InMemoryUserDetailsManager(admin, pracownik, student);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/login", "/public/**").permitAll()
                        .requestMatchers("/pracownicy/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/sale/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/przedmioty/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/oceny/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .requestMatchers("/kierunki/**").hasAnyRole("ADMIN", "PRACOWNIK")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/pracownicy", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}