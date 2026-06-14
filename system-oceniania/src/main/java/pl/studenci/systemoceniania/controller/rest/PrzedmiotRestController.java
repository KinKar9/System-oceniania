package pl.studenci.systemoceniania.controller.rest;

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

    @GetMapping("/{id}")
    public ResponseEntity<Przedmiot> getOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Przedmiot> create(@RequestBody Przedmiot przedmiot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(przedmiot));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Przedmiot> update(@PathVariable Long id, @RequestBody Przedmiot przedmiot) {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}