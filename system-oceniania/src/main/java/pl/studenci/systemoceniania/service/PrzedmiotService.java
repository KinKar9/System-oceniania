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
import pl.studenci.systemoceniania.entity.Przedmiot;
import pl.studenci.systemoceniania.repository.PrzedmiotRepository;

import java.util.List;

@Service
@Transactional
public class PrzedmiotService {

    private static final Logger log = LoggerFactory.getLogger(PrzedmiotService.class);
    private final PrzedmiotRepository repository;

    public PrzedmiotService(PrzedmiotRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Przedmiot> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy przedmiotów: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się pobrać przedmiotów");
        }
    }

    @Transactional(readOnly = true)
    public Page<Przedmiot> findAll(Pageable pageable) {
        try {
            return repository.findAll(pageable);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania stronnicowanej listy przedmiotów: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się pobrać przedmiotów");
        }
    }

    @Transactional(readOnly = true)
    public Przedmiot findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba wyszukania przedmiotu z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID przedmiotu");
        }
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono przedmiotu o ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono przedmiotu o ID: " + id);
                });
    }

    // Metody modyfikujące – dziedziczą @Transactional z klasy
    public Przedmiot save(Przedmiot przedmiot) {
        if (przedmiot == null) {
            log.error("Próba zapisu null jako przedmiot");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Przedmiot nie może być nullem");
        }

        if (przedmiot.getKodPrzedmiotu() == null || przedmiot.getKodPrzedmiotu().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kod przedmiotu jest wymagany");
        }
        if (przedmiot.getNazwa() == null || przedmiot.getNazwa().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nazwa przedmiotu jest wymagana");
        }
        if (przedmiot.getEcts() == null || przedmiot.getEcts() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ECTS musi być dodatnią liczbą");
        }
        if (przedmiot.getKierunek() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kierunek jest wymagany");
        }

        try {
            Przedmiot saved = repository.save(przedmiot);
            log.info("Zapisano przedmiot: {} (ID: {})", przedmiot.getNazwa(), saved.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("Naruszenie unikalności przy zapisie przedmiotu: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Przedmiot o podanym kodzie lub nazwie już istnieje");
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas zapisu przedmiotu: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się zapisać przedmiotu");
        }
    }

    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba usunięcia przedmiotu z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID przedmiotu");
        }
        if (!repository.existsById(id)) {
            log.warn("Próba usunięcia nieistniejącego przedmiotu o ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Przedmiot o ID " + id + " nie istnieje");
        }
        try {
            repository.deleteById(id);
            log.info("Usunięto przedmiot o ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Nie można usunąć przedmiotu o ID {} – może być powiązany z grupami lub ocenami", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można usunąć przedmiotu, ponieważ jest używany");
        } catch (Exception e) {
            log.error("Błąd podczas usuwania przedmiotu o ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się usunąć przedmiotu");
        }
    }
}