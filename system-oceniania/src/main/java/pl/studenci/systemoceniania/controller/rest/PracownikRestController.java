package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Pobiera wszystkich pracowników")
    @ApiResponse(responseCode = "200", description = "Lista pracowników")
    public ResponseEntity<List<Pracownik>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera pracownika po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono pracownika"),
            @ApiResponse(responseCode = "404", description = "Pracownik nie istnieje")
    })
    public ResponseEntity<Pracownik> getOne(@Parameter(description = "ID pracownika") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Tworzy nowego pracownika")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pracownik utworzony"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<Pracownik> create(@RequestBody Pracownik pracownik) {
        Pracownik saved = service.save(pracownik);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje pracownika")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pracownik zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Pracownik nie istnieje")
    })
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
    @Operation(summary = "Usuwa pracownika")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Pracownik nie istnieje")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.findById(id); // sprawdzenie czy istnieje
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}