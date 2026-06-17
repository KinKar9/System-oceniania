package pl.studenci.systemoceniania.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.repository.UzytkownikRepository;

import java.util.Collections;
import java.util.List;
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
            throw new UsernameNotFoundException("Nieprawidłowe dane logowania");
        }

        List<GrantedAuthority> authorities = (uzytkownik.getRole() != null)
                ? uzytkownik.getRole().stream()
                .map(rola -> new SimpleGrantedAuthority("ROLE_" + rola.getNazwaRoli().name()))
                .collect(Collectors.toList())
                : Collections.emptyList();

        log.debug("Załadowano użytkownika: {} z rolami: {}", username, authorities);

        return User.builder()
                .username(uzytkownik.getUsername())
                .password(uzytkownik.getPassword())
                .authorities(authorities)
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
            log.error("Błąd podczas zapisu użytkownika: {}", e.getMessage(), e);
            throw new RuntimeException("Nie udało się zapisać użytkownika", e);
        }
    }

    @Transactional(readOnly = true)
    public Uzytkownik findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Nazwa użytkownika nie może być pusta");
        }
        return uzytkownikRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono użytkownika o nazwie: {}", username);
                    return new UsernameNotFoundException("Brak użytkownika: " + username);
                });
    }

    @Transactional(readOnly = true)
    public Uzytkownik findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email nie może być pusty");
        }
        return uzytkownikRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono użytkownika z emailem: {}", email);
                    throw new RuntimeException("Nie znaleziono użytkownika z emailem: " + email);
                });
    }

    // 🔥 METODA Z Integer
    @Transactional(readOnly = true)
    public Uzytkownik findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Nieprawidłowe ID użytkownika");
        }
        return uzytkownikRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik o ID " + id + " nie istnieje"));
    }

    // 🔥 ZMIENIONA NA Integer
    @Transactional
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Nieprawidłowe ID użytkownika");
        }
        Uzytkownik uzytkownik = findById(id);
        uzytkownikRepository.delete(uzytkownik);
        log.info("Usunięto użytkownika o ID: {}", id);
    }

    // 🔥 METODA findAllPracownicy
    @Transactional(readOnly = true)
    public List<Uzytkownik> findAllPracownicy() {
        List<Uzytkownik> wszyscy = uzytkownikRepository.findAll();
        return wszyscy.stream()
                .filter(u -> u.getRole() != null)
                .filter(u -> u.getRole().stream().anyMatch(r -> r.getNazwaRoli().name().equals("PRACOWNIK")))
                .collect(Collectors.toList());
    }
}