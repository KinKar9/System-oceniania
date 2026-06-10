package pl.studenci.systemoceniania.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.service.StudentService;
import java.util.List;

@RestController
@RequestMapping("/api/studenci")
public class StudentRestController {

    private final StudentService service;

    public StudentRestController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@RequestBody Student student) {
        return service.save(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        try {
            Student existing = service.findById(id);
            existing.setImie(student.getImie());
            existing.setNazwisko(student.getNazwisko());
            existing.setEmail(student.getEmail());
            existing.setNrIndeksu(student.getNrIndeksu());
            existing.setDataUrodzenia(student.getDataUrodzenia());
            existing.setPesel(student.getPesel());
            return ResponseEntity.ok(service.save(existing));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}