package pl.studenci.systemoceniania.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.repository.StudentRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    // POPRAWKA 1: pole nazywa się "repository" — było "studentRepository" w dodanej metodzie
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy studentów: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Nie udało się pobrać studentów");
        }
    }

    public Student findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba wyszukania studenta z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowe ID studenta");
        }
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono studenta o ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Nie znaleziono studenta o ID: " + id);
                });
    }

    @Transactional
    public Student save(Student student) {
        if (student == null) {
            log.error("Próba zapisu null jako student");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Student nie może być nullem");
        }
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email studenta jest wymagany");
        }
        if (student.getNrIndeksu() == null || student.getNrIndeksu().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Numer indeksu jest wymagany");
        }
        try {
            Student saved = repository.save(student);
            log.info("Zapisano studenta: {} {} (ID: {})",
                    saved.getImie(), saved.getNazwisko(), saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Błąd podczas zapisu studenta: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Nie udało się zapisać studenta");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba usunięcia studenta z nieprawidłowym ID: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nieprawidłowe ID studenta");
        }
        // POPRAWKA 2: jedno zapytanie zamiast existsById + deleteById
        Student student = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Próba usunięcia nieistniejącego studenta o ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Student o ID " + id + " nie istnieje");
                });
        try {
            repository.delete(student);
            log.info("Usunięto studenta o ID: {}", id);
        } catch (Exception e) {
            log.error("Błąd podczas usuwania studenta o ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Nie udało się usunąć studenta");
        }
    }

    public Student findByEmail(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Próba wyszukania studenta z pustym emailem");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email nie może być pusty");
        }
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono studenta z emailem: {}", email);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Nie znaleziono studenta z emailem: " + email);
                });
    }

    // POPRAWKA 3: usunięto findBySecureToken rzucający NOT_FOUND —
    // kontroler PublicStudentController używa findBySecureTokenWithOceny,
    // które zwraca null zamiast wyjątku, żeby nie ujawniać istnienia tokenu
    @Transactional(readOnly = true)
    public Student findBySecureTokenWithOceny(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        // POPRAWKA 1: było "studentRepository" — poprawiono na "repository"
        return repository.findBySecureTokenWithOceny(token).orElse(null);
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 6) return "***";
        return token.substring(0, 3) + "..." + token.substring(token.length() - 3);
    }
}