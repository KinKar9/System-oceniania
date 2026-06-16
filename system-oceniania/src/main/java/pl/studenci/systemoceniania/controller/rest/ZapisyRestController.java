package pl.studenci.systemoceniania.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Zapisy;
import pl.studenci.systemoceniania.service.ZapisyService;

import java.util.List;

@RestController
@RequestMapping("/api/zapisy")
public class ZapisyRestController {

    private final ZapisyService service;

    public ZapisyRestController(ZapisyService service) {
        this.service = service;
    }

    @GetMapping
    public List<Zapisy> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zapisy> getOne(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/zapisz")
    public ResponseEntity<?> zapiszStudenta(@RequestParam Long studentId, @RequestParam Long grupaId) {
        try {
            Zapisy zapis = service.zapiszStudenta(studentId, grupaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(zapis);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/wypisz/{id}")
    public ResponseEntity<Void> wypiszStudenta(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.wypiszStudenta(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);   // wymaga metody delete w ZapisyService (dodajemy osobno)
        return ResponseEntity.noContent().build();
    }
}