package pl.studenci.systemoceniania.service;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.repository.PracownikRepository;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class PracownikService {

    private static final Logger log = LoggerFactory.getLogger(PracownikService.class);
    private final PracownikRepository repository;

    public PracownikService(PracownikRepository repository) {
        this.repository = repository;
    }

    // Zwraca wszystkich pracowników (bez paginacji – dla małych zbiorów)
    public List<Pracownik> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy pracowników: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się pobrać pracowników");
        }
    }

    // Paginacja – dla dużych zbiorów
    public Page<Pracownik> findAll(Pageable pageable) {
        try {
            return repository.findAll(pageable);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania stronnicowanej listy pracowników: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się pobrać pracowników");
        }
    }

    public Pracownik findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba wyszukania pracownika z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID pracownika");
        }
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono pracownika o ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono pracownika o ID: " + id);
                });
    }

    public Pracownik save(Pracownik pracownik) {
        if (pracownik == null) {
            log.error("Próba zapisu null jako pracownik");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pracownik nie może być nullem");
        }

        // Walidacja podstawowych pól
        if (pracownik.getImie() == null || pracownik.getImie().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imię pracownika jest wymagane");
        }
        if (pracownik.getNazwisko() == null || pracownik.getNazwisko().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nazwisko pracownika jest wymagane");
        }
        if (pracownik.getEmail() == null || pracownik.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email pracownika jest wymagany");
        }
        if (!pracownik.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowy format email");
        }

        try {
            Pracownik saved = repository.save(pracownik);
            log.info("Zapisano pracownika: {} {} (ID: {})", saved.getImie(), saved.getNazwisko(), saved.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("Naruszenie unikalności przy zapisie pracownika: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pracownik o podanym emailu już istnieje");
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas zapisu pracownika: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się zapisać pracownika");
        }
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba usunięcia pracownika z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID pracownika");
        }

        if (!repository.existsById(id)) {
            log.warn("Próba usunięcia nieistniejącego pracownika o ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pracownik o ID " + id + " nie istnieje");
        }

        try {
            repository.deleteById(id);
            log.info("Usunięto pracownika o ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Nie można usunąć pracownika o ID {} – może prowadzić grupy", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można usunąć pracownika, ponieważ prowadzi grupy lub ma inne powiązania");
        } catch (Exception e) {
            log.error("Błąd podczas usuwania pracownika o ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się usunąć pracownika");
        }
    }
}