package pl.studenci.systemoceniania.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Ranking;
import pl.studenci.systemoceniania.service.RankingService;
import pl.studenci.systemoceniania.service.StatystykiService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/statystyki")
public class StatystykiRestController {

    private final StatystykiService statystykiService;
    private final RankingService rankingService;

    public StatystykiRestController(StatystykiService statystykiService, RankingService rankingService) {
        this.statystykiService = statystykiService;
        this.rankingService = rankingService;
    }

    @GetMapping("/srednia/{studentId}")
    @Operation(summary = "Pobiera średnią ocen studenta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Średnia zwrócona"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje lub brak ocen")
    })
    public ResponseEntity<Map<String, Double>> getSrednia(@Parameter(description = "ID studenta") @PathVariable Long studentId) {
        Optional<Double> srednia = statystykiService.pobierzSredniaStudenta(studentId);
        if (srednia.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Double> response = new HashMap<>();
        response.put("srednia", srednia.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ranking")
    @Operation(summary = "Generuje ranking (na podstawie semestru)")
    @ApiResponse(responseCode = "200", description = "Ranking wygenerowany")
    public ResponseEntity<Void> generujRanking(@RequestParam(required = false) String idSemestru) {
        rankingService.generateRanking(idSemestru);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ranking/ostatni")
    @Operation(summary = "Pobiera ostatni wygenerowany ranking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking znaleziony"),
            @ApiResponse(responseCode = "404", description = "Brak rankingu")
    })
    public ResponseEntity<Ranking> getOstatniRanking() {
        Ranking ranking = rankingService.getLatestRanking().orElse(null);
        if (ranking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ranking);
    }

    @PostMapping("/sprawdz-zaliczenie/{studentId}")
    @Operation(summary = "Sprawdza zaliczenie semestru dla studenta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zaliczenie sprawdzone"),
            @ApiResponse(responseCode = "404", description = "Student nie istnieje")
    })
    public ResponseEntity<Void> sprawdzZaliczenie(@Parameter(description = "ID studenta") @PathVariable Long studentId) {
        // Zakładam, że metoda w serwisie rzuca wyjątek jeśli student nie istnieje
        try {
            statystykiService.sprawdzZaliczenie(studentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}