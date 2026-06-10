package pl.studenci.systemoceniania.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.service.OcenaService;

import java.util.List;

@RestController
@RequestMapping("/api/oceny")
public class OcenaRestController {

    private final OcenaService ocenaService;

    public OcenaRestController(OcenaService ocenaService) {
        this.ocenaService = ocenaService;
    }

    @GetMapping
    public List<Ocena> getAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long przedmiotId,
            @RequestParam(required = false) Long typId,
            @RequestParam(defaultValue = "data") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        return ocenaService.filterAndSort(studentId, przedmiotId, typId, sortBy, order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ocena> getOne(@PathVariable Long id) {
        Ocena ocena = ocenaService.findById(id);
        return ocena != null ? ResponseEntity.ok(ocena) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Ocena ocena) {
        try {
            Ocena saved = ocenaService.save(ocena);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Ocena ocenaDetails) {
        Ocena existing = ocenaService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setWartosc(ocenaDetails.getWartosc());
        existing.setKomentarz(ocenaDetails.getKomentarz());
        if (ocenaDetails.getTyp() != null) existing.setTyp(ocenaDetails.getTyp());
        if (ocenaDetails.getZapis() != null && ocenaDetails.getZapis().getId() != null) {
            existing.setZapis(ocenaDetails.getZapis());
        }
        try {
            Ocena updated = ocenaService.save(existing);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ocenaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}