package pl.studenci.systemoceniania.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.studenci.systemoceniania.entity.PozycjaPlanu;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.service.PlanZajecService;

import java.util.List;

@RestController
@RequestMapping("/api/student/plan")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPlanRestController {

    private final PlanZajecService planService;

    public StudentPlanRestController(PlanZajecService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PozycjaPlanu>> getMyPlan(Authentication auth) {
        Uzytkownik student = (Uzytkownik) auth.getPrincipal();
        // 🔥 KONWERSJA: Long → Integer
        List<PozycjaPlanu> plan = planService.getPlanForStudent(student.getId().intValue());
        return ResponseEntity.ok(plan);
    }
}