package pl.studenci.systemoceniania.controller.rest;

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

    @GetMapping("/{id}")
    public ResponseEntity<Sala> getOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Sala> create(@RequestBody Sala sala) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(sala));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sala> update(@PathVariable Long id, @RequestBody Sala sala) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}