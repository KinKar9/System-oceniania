package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Pobiera wszystkie kierunki")
    @ApiResponse(responseCode = "200", description = "Lista kierunków")
    public ResponseEntity<List<Kierunek>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera kierunek po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono kierunek"),
            @ApiResponse(responseCode = "404", description = "Kierunek nie istnieje")
    })
    public ResponseEntity<Kierunek> getOne(@Parameter(description = "ID kierunku") @PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Tworzy nowy kierunek")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Kierunek utworzony"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<Kierunek> create(@RequestBody Kierunek kierunek) {
        Kierunek saved = repository.save(kierunek);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje kierunek")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kierunek zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Kierunek nie istnieje")
    })
    public ResponseEntity<Kierunek> update(@PathVariable Long id, @RequestBody Kierunek kierunek) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        kierunek.setId(id);
        return ResponseEntity.ok(repository.save(kierunek));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuwa kierunek")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Kierunek nie istnieje")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}