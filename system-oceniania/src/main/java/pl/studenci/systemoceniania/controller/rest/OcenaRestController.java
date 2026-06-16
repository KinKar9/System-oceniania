package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.service.OcenaService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/oceny")
public class OcenaRestController {

    private final OcenaService ocenaService;

    public OcenaRestController(OcenaService ocenaService) {
        this.ocenaService = ocenaService;
    }

    @GetMapping
    @Operation(summary = "Pobiera oceny z filtrowaniem i sortowaniem",
            description = "Filtruje po studentId, przedmiotId, typId i dacie od. Sortowanie zapamiętywane w ciasteczku.")
    @ApiResponse(responseCode = "200", description = "Lista ocen")
    public ResponseEntity<List<Ocena>> getAll(
            @Parameter(description = "ID studenta") @RequestParam(required = false) Long studentId,
            @Parameter(description = "ID przedmiotu") @RequestParam(required = false) Long przedmiotId,
            @Parameter(description = "ID typu oceny") @RequestParam(required = false) Long typId,
            @Parameter(description = "Data od (domyślnie dzisiaj)") @RequestParam(required = false) LocalDate dataOd,
            @Parameter(description = "Pole sortowania (np. data, wartosc)") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Kierunek sortowania (asc/desc)") @RequestParam(required = false) String order,
            @CookieValue(name = "sortPreference", required = false) String cookieSort,
            HttpServletResponse response) {

        if (dataOd == null) {
            dataOd = LocalDate.now();
        }

        if (sortBy == null && cookieSort != null && cookieSort.contains(",")) {
            String[] parts = cookieSort.split(",");
            sortBy = parts[0];
            order = parts.length > 1 ? parts[1] : "desc";
        }
        if (sortBy == null) sortBy = "data";
        if (order == null) order = "desc";

        ResponseCookie cookie = ResponseCookie.from("sortPreference", sortBy + "," + order)
                .httpOnly(true)
                .maxAge(Duration.ofDays(30))
                .path("/api/oceny")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        List<Ocena> oceny = ocenaService.filterAndSort(studentId, przedmiotId, typId, dataOd, sortBy, order);
        return ResponseEntity.ok(oceny);
    }

    @GetMapping("/popularne")
    @Operation(summary = "Pobiera oceny posortowane według popularności przedmiotu")
    @ApiResponse(responseCode = "200", description = "Lista ocen")
    public ResponseEntity<List<Ocena>> getSortedByPopularnoscPrzedmiotu() {
        return ResponseEntity.ok(ocenaService.findAllSortedByPopularnoscPrzedmiotu());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pobiera ocenę po ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Znaleziono ocenę"),
            @ApiResponse(responseCode = "404", description = "Ocena nie istnieje")
    })
    public ResponseEntity<Ocena> getOne(@Parameter(description = "ID oceny") @PathVariable Long id) {
        Ocena ocena = ocenaService.findById(id);
        return ocena != null ? ResponseEntity.ok(ocena) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Tworzy nową ocenę")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ocena utworzona"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<?> create(@Valid @RequestBody Ocena ocena) {
        try {
            Ocena saved = ocenaService.save(ocena);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aktualizuje ocenę")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ocena zaktualizowana"),
            @ApiResponse(responseCode = "404", description = "Ocena nie istnieje"),
            @ApiResponse(responseCode = "400", description = "Błędne dane")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Ocena ocenaDetails) {
        Ocena existing = ocenaService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
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
    @Operation(summary = "Usuwa ocenę")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Ocena nie istnieje")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (ocenaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        ocenaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}