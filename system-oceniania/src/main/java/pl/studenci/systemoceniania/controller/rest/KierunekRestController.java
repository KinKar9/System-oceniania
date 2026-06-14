package pl.studenci.systemoceniania.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Kierunek;
import pl.studenci.systemoceniania.repository.KierunekRepository;

import java.util.List;

@RestController
@RequestMapping("/api/kierunki")
public class KierunekRestController {

    private final KierunekRepository repository;

    public KierunekRestController(KierunekRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Kierunek> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kierunek> getOne(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Kierunek create(@RequestBody Kierunek kierunek) {
        return repository.save(kierunek);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kierunek> update(@PathVariable Long id, @RequestBody Kierunek kierunek) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        kierunek.setId(id);
        return ResponseEntity.ok(repository.save(kierunek));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}