package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Pobiera wszystkie sale")
    @ApiResponse(responseCode = "200", description = "Lista sal")
    public ResponseEntity<List<Sala>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera salę po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono salę"),
            @ApiResponse(responseCode = "404", description = "Sala nie istnieje")
    })
    public ResponseEntity<Sala> getOne(@Parameter(description = "ID sali") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Tworzy nową salę")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sala utworzona"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<Sala> create(@RequestBody Sala sala) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(sala));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje salę")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sala zaktualizowana"),
            @ApiResponse(responseCode = "404", description = "Sala nie istnieje")
    })
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
    @Operation(summary = "Usuwa salę")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Sala nie istnieje")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.findById(id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}