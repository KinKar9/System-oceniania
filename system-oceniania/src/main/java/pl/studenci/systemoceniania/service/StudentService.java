package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Student findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Student save(Student student) {
        return repository.save(student);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Student findBySecureToken(String token) {
        return repository.findBySecureToken(token)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Nieprawidłowy lub wygasły token dostępu."));
    }
}