package pl.studenci.systemoceniania.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Przedmiot;
import pl.studenci.systemoceniania.service.PrzedmiotService;

import java.util.List;

@RestController
@RequestMapping("/api/przedmioty")
public class PrzedmiotRestController {

    private final PrzedmiotService service;

    public PrzedmiotRestController(PrzedmiotService service) {
        this.service = service;
    }

    @GetMapping
    public List<Przedmiot> getAll() {
        return service.findAll();
    }

    // 🔥 ZMIENIONO: Long → Integer
    @GetMapping("/{id}")
    public ResponseEntity<Przedmiot> getOne(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Przedmiot> create(@Valid @RequestBody Przedmiot przedmiot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(przedmiot));
    }

    // 🔥 ZMIENIONO: Long → Integer
    @PutMapping("/{id}")
    public ResponseEntity<Przedmiot> update(@PathVariable Integer id, @Valid @RequestBody Przedmiot przedmiot) {
        try {
            Przedmiot existing = service.findById(id);
            existing.setNazwa(przedmiot.getNazwa());
            existing.setKodPrzedmiotu(przedmiot.getKodPrzedmiotu());
            existing.setEcts(przedmiot.getEcts());
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