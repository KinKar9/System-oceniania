package pl.studenci.systemoceniania.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Sala;
import pl.studenci.systemoceniania.service.SalaService;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class SalaRestController {

    private final SalaService service;

    public SalaRestController(SalaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sala> getAll() {
        return service.findAll();
    }

    // 🔥 ZMIENIONO: Long → Integer
    @GetMapping("/{id}")
    public ResponseEntity<Sala> getOne(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Sala> create(@Valid @RequestBody Sala sala) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(sala));
    }

    // 🔥 ZMIENIONO: Long → Integer
    @PutMapping("/{id}")
    public ResponseEntity<Sala> update(@PathVariable Integer id, @Valid @RequestBody Sala sala) {
        try {
            Sala existing = service.findById(id);
            existing.setNumerSali(sala.getNumerSali());
            existing.setPojemnosc(sala.getPojemnosc());
            existing.setTypSali(sala.getTypSali());
            return ResponseEntity.ok(service.save(existing));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔥 ZMIENIONO: Long → Integer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}