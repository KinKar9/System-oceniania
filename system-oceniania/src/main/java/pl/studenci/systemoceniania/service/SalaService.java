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
import pl.studenci.systemoceniania.entity.Sala;
import pl.studenci.systemoceniania.repository.SalaRepository;

import java.util.List;

@Service
@Transactional
public class SalaService {

    private static final Logger log = LoggerFactory.getLogger(SalaService.class);
    private final SalaRepository repository;

    public SalaService(SalaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Sala> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy sal: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się pobrać sal");
        }
    }

    @Transactional(readOnly = true)
    public Page<Sala> findAll(Pageable pageable) {
        try {
            return repository.findAll(pageable);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania stronnicowanej listy sal: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się pobrać sal");
        }
    }

    // 🔥 METODA Z Integer
    @Transactional(readOnly = true)
    public Sala findById(Integer id) {
        if (id == null || id <= 0) {
            log.warn("Próba wyszukania sali z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID sali");
        }
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono sali o ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono sali o ID: " + id);
                });
    }

    public Sala save(Sala sala) {
        if (sala == null) {
            log.error("Próba zapisu null jako sala");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sala nie może być nullem");
        }
        if (sala.getNumerSali() == null || sala.getNumerSali().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numer sali jest wymagany");
        }
        if (sala.getPojemnosc() == null || sala.getPojemnosc() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pojemność musi być dodatnia");
        }
        try {
            Sala saved = repository.save(sala);
            log.info("Zapisano salę: {} (ID: {})", sala.getNumerSali(), saved.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("Naruszenie unikalności przy zapisie sali: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala o podanym numerze już istnieje");
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas zapisu sali: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się zapisać sali");
        }
    }

    // 🔥 ZMIENIONE NA Integer
    public void delete(Integer id) {
        if (id == null || id <= 0) {
            log.warn("Próba usunięcia sali z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID sali");
        }
        if (!repository.existsById(id)) {
            log.warn("Próba usunięcia nieistniejącej sali o ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala o ID " + id + " nie istnieje");
        }
        try {
            repository.deleteById(id);
            log.info("Usunięto salę o ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Nie można usunąć sali o ID {} – może być powiązana z innymi rekordami", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można usunąć sali, ponieważ jest używana");
        } catch (Exception e) {
            log.error("Błąd podczas usuwania sali o ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się usunąć sali");
        }
    }
}