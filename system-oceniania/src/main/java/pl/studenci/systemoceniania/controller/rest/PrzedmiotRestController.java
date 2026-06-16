package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Pobiera wszystkie przedmioty")
    @ApiResponse(responseCode = "200", description = "Lista przedmiotów")
    public ResponseEntity<List<Przedmiot>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera przedmiot po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono przedmiot"),
            @ApiResponse(responseCode = "404", description = "Przedmiot nie istnieje")
    })
    public ResponseEntity<Przedmiot> getOne(@Parameter(description = "ID przedmiotu") @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Tworzy nowy przedmiot")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Przedmiot utworzony"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<Przedmiot> create(@RequestBody Przedmiot przedmiot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(przedmiot));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje przedmiot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Przedmiot zaktualizowany"),
            @ApiResponse(responseCode = "404", description = "Przedmiot nie istnieje")
    })
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
    @Operation(summary = "Usuwa przedmiot")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Przedmiot nie istnieje")
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