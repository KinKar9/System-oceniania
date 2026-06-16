package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Pobiera wszystkich studentów")
    @ApiResponse(responseCode = "200", description = "Lista studentów")
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera studenta po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono studenta"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    public ResponseEntity<Student> getOne(@Parameter(description = "ID studenta") @PathVariable Long id) {
        Student student = service.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    @Operation(summary = "Tworzy nowego studenta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student utworzony"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<Student> create(@RequestBody Student student) {
        Student saved = service.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje studenta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        Student existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setImie(student.getImie());
        existing.setNazwisko(student.getNazwisko());
        existing.setEmail(student.getEmail());
        existing.setNrIndeksu(student.getNrIndeksu());
        existing.setDataUrodzenia(student.getDataUrodzenia());
        existing.setPesel(student.getPesel());
        return ResponseEntity.ok(service.save(existing));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuwa studenta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}