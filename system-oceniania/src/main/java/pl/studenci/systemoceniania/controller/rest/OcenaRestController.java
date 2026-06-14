package pl.studenci.systemoceniania.controller.rest;

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

    // Główny endpoint z filtrowaniem, sortowaniem i zapamiętywaniem sortowania w ciasteczku
    @GetMapping
    public List<Ocena> getAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long przedmiotId,
            @RequestParam(required = false) Long typId,
            @RequestParam(required = false) LocalDate dataOd,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order,
            @CookieValue(name = "sortPreference", required = false) String cookieSort,
            HttpServletResponse response) {

        // 1. Domyślne filtrowanie od aktualnej daty (jeśli nie podano)
        if (dataOd == null) {
            dataOd = LocalDate.now();
        }

        // 2. Odczytanie preferencji sortowania z ciasteczka, jeśli nie podano parametrów
        if (sortBy == null && cookieSort != null && cookieSort.contains(",")) {
            String[] parts = cookieSort.split(",");
            sortBy = parts[0];
            order = parts.length > 1 ? parts[1] : "desc";
        }
        // Domyślne wartości
        if (sortBy == null) sortBy = "data";
        if (order == null) order = "desc";

        // 3. Zapisanie nowego ciasteczka (zawsze aktualizujemy, aby odzwierciedlić ostatnie sortowanie)
        ResponseCookie cookie = ResponseCookie.from("sortPreference", sortBy + "," + order)
                .httpOnly(true)
                .maxAge(Duration.ofDays(30))
                .path("/api/oceny")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Wywołanie serwisu
        return ocenaService.filterAndSort(studentId, przedmiotId, typId, dataOd, sortBy, order);
    }

    // Endpoint do sortowania według popularności przedmiotu (najwięcej ocen)
    @GetMapping("/popularne")
    public List<Ocena> getSortedByPopularnoscPrzedmiotu() {
        return ocenaService.findAllSortedByPopularnoscPrzedmiotu();
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