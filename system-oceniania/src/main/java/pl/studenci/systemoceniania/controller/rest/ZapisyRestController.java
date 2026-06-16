package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Zapisy;
import pl.studenci.systemoceniania.service.ZapisyService;

import java.util.List;

@RestController
@RequestMapping("/api/zapisy")
public class ZapisyRestController {

    private final ZapisyService service;

    public ZapisyRestController(ZapisyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Pobiera wszystkie zapisy")
    @ApiResponse(responseCode = "200", description = "Lista zapisów")
    public ResponseEntity<List<Zapisy>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera zapis po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono zapis"),
            @ApiResponse(responseCode = "404", description = "Zapis nie istnieje")
    })
    public ResponseEntity<Zapisy> getOne(@Parameter(description = "ID zapisu") @PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/zapisz")
    @Operation(summary = "Zapisuje studenta na grupę")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student zapisany"),
            @ApiResponse(responseCode = "400", description = "Błąd zapisu (np. grupa pełna)")
    })
    public ResponseEntity<?> zapiszStudenta(
            @Parameter(description = "ID studenta") @RequestParam Long studentId,
            @Parameter(description = "ID grupy") @RequestParam Long grupaId) {
        try {
            Zapisy zapis = service.zapiszStudenta(studentId, grupaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(zapis);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/wypisz/{id}")
    @Operation(summary = "Wypisuje studenta z zapisu")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Wypisano"),
            @ApiResponse(responseCode = "404", description = "Zapis nie istnieje")
    })
    public ResponseEntity<Void> wypiszStudenta(@Parameter(description = "ID zapisu") @PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.wypiszStudenta(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Usuwa zapis")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Zapis nie istnieje")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID zapisu") @PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}