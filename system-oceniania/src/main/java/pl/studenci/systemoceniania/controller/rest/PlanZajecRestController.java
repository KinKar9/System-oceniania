package pl.studenci.systemoceniania.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.PlanZajec;
import pl.studenci.systemoceniania.entity.PozycjaPlanu;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.service.PlanZajecService;

import java.util.List;

@RestController
@RequestMapping("/api/pracownik/plany")
@PreAuthorize("hasRole('PRACOWNIK')")
public class PlanZajecRestController {

    private final PlanZajecService planService;

    public PlanZajecRestController(PlanZajecService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<PlanZajec> createPlan(@Valid @RequestBody PlanZajec plan) {
        return ResponseEntity.ok(planService.createPlan(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanZajec> updatePlan(
            @PathVariable Integer id,
            @Valid @RequestBody PlanZajec plan) {
        return ResponseEntity.ok(planService.updatePlan(id, plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PlanZajec>> getMyPlans(Authentication auth) {
        Uzytkownik pracownik = (Uzytkownik) auth.getPrincipal();
        // 🔥 KONWERSJA: Long → Integer
        List<PlanZajec> plany = planService.getPlansForPracownik(pracownik.getId().intValue());
        return ResponseEntity.ok(plany);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanZajec> getPlanById(@PathVariable Integer id) {
        return ResponseEntity.ok(planService.findPlanById(id));
    }

    @PostMapping("/{planId}/pozycje")
    public ResponseEntity<PozycjaPlanu> addPozycja(
            @PathVariable Integer planId,
            @Valid @RequestBody PozycjaPlanu pozycja) {
        return ResponseEntity.ok(planService.addPozycja(planId, pozycja));
    }

    @PutMapping("/pozycje/{pozycjaId}")
    public ResponseEntity<PozycjaPlanu> updatePozycja(
            @PathVariable Integer pozycjaId,
            @Valid @RequestBody PozycjaPlanu pozycja) {
        return ResponseEntity.ok(planService.updatePozycja(pozycjaId, pozycja));
    }

    @DeleteMapping("/pozycje/{pozycjaId}")
    public ResponseEntity<Void> deletePozycja(@PathVariable Integer pozycjaId) {
        planService.deletePozycja(pozycjaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pozycje/{pozycjaId}")
    public ResponseEntity<PozycjaPlanu> getPozycjaById(@PathVariable Integer pozycjaId) {
        return ResponseEntity.ok(planService.findPozycjaById(pozycjaId));
    }
}