package pl.studenci.systemoceniania.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.service.PracownikService;
import java.util.List;

@RestController
@RequestMapping("/api/pracownicy")
public class PracownikRestController {

    private final PracownikService service;

    public PracownikRestController(PracownikService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pracownik> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pracownik> getOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Pracownik> create(@RequestBody Pracownik pracownik) {
        Pracownik saved = service.save(pracownik);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pracownik> update(@PathVariable Long id, @RequestBody Pracownik pracownik) {
        try {
            Pracownik existing = service.findById(id);
            existing.setImie(pracownik.getImie());
            existing.setNazwisko(pracownik.getNazwisko());
            existing.setTytulNaukowy(pracownik.getTytulNaukowy());
            existing.setEmail(pracownik.getEmail());
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