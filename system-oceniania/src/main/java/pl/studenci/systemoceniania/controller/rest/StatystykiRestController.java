package pl.studenci.systemoceniania.controller.rest;

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
    public Map<String, Double> getSrednia(@PathVariable Long studentId) {
        Optional<Double> srednia = statystykiService.pobierzSredniaStudenta(studentId);
        Map<String, Double> response = new HashMap<>();
        response.put("srednia", srednia.orElse(null));
        return response;
    }

    @PostMapping("/ranking")
    public void generujRanking(@RequestParam(required = false) String idSemestru) {
        rankingService.generateRanking(idSemestru);
    }

    @GetMapping("/ranking/ostatni")
    public Ranking getOstatniRanking() {
        return rankingService.getLatestRanking().orElse(null);
    }

    @PostMapping("/sprawdz-zaliczenie/{studentId}")
    public void sprawdzZaliczenie(@PathVariable Long studentId) {
        statystykiService.sprawdzZaliczenie(studentId);
    }
}