package pl.studenci.systemoceniania.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.repository.UzytkownikRepository;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UzytkownikService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UzytkownikService.class);
    private final UzytkownikRepository uzytkownikRepository;

    public UzytkownikService(UzytkownikRepository uzytkownikRepository) {
        this.uzytkownikRepository = uzytkownikRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            log.warn("Próba logowania z pustą nazwą użytkownika");
            throw new UsernameNotFoundException("Nazwa użytkownika nie może być pusta");
        }

        Uzytkownik uzytkownik = uzytkownikRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono użytkownika o nazwie: {}", username);
                    return new UsernameNotFoundException("Nieprawidłowe dane logowania");
                });

        if (!uzytkownik.isCzyAktywny()) {
            log.warn("Próba logowania nieaktywnego użytkownika: {}", username);
            // POPRAWKA 1: ogólny komunikat — nie zdradza czy konto istnieje czy jest nieaktywne
            throw new UsernameNotFoundException("Nieprawidłowe dane logowania");
        }
        log.debug("Załadowano użytkownika: {}", username);
        return User.builder()
                .username(uzytkownik.getUsername())
                .password(uzytkownik.getPassword())
                .authorities(uzytkownik.getRole().stream()
                        .map(rola -> new SimpleGrantedAuthority("ROLE_" + rola.getNazwaRoli()))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public Uzytkownik save(Uzytkownik uzytkownik) {
        if (uzytkownik == null) {
            throw new IllegalArgumentException("Użytkownik nie może być null");
        }
        try {
            Uzytkownik saved = uzytkownikRepository.save(uzytkownik);
            log.info("Zapisano użytkownika: {}", saved.getUsername());
            return saved;
        } catch (Exception e) {
            log.error("Błąd podczas zapisu użytkownika: {}", e.getMessage());
            throw new RuntimeException("Nie udało się zapisać użytkownika", e);
        }
    }
    @Transactional(readOnly = true)
    public Uzytkownik findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nazwa użytkownika nie może być pusta");
        }
        Uzytkownik uzytkownik = uzytkownikRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono użytkownika o nazwie: {}", username);
                    return new UsernameNotFoundException("Brak użytkownika: " + username);
                });
        if (uzytkownik.getStudent() != null) {
            uzytkownik.getStudent().getId();
        }

        return uzytkownik;
    }

    public Uzytkownik findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email nie może być pusty");
        }
        return uzytkownikRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono użytkownika z emailem: {}", email);
                    return new RuntimeException("Nie znaleziono użytkownika z emailem: " + email);
                });
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Nieprawidłowe ID użytkownika");
        }
        // POPRAWKA 4: jedno zapytanie zamiast existsById + deleteById
        Uzytkownik uzytkownik = uzytkownikRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Próba usunięcia nieistniejącego użytkownika o ID: {}", id);
                    return new RuntimeException("Użytkownik o podanym ID nie istnieje");
                });
        uzytkownikRepository.delete(uzytkownik);
        log.info("Usunięto użytkownika o ID: {}", id);
    }
}