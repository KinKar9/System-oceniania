package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.studenci.systemoceniania.entity.PozycjaPlanu;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.service.PlanZajecService;
import pl.studenci.systemoceniania.service.UzytkownikService;

import java.util.List;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPlanViewController {

    private static final Logger log = LoggerFactory.getLogger(StudentPlanViewController.class);

    private final PlanZajecService planService;
    private final UzytkownikService uzytkownikService;

    public StudentPlanViewController(PlanZajecService planService,
                                     UzytkownikService uzytkownikService) {
        this.planService = planService;
        this.uzytkownikService = uzytkownikService;
    }

    @GetMapping("/plan")
    public String showPlan(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            Uzytkownik uzytkownik = uzytkownikService.findByUsername(auth.getName());
            if (uzytkownik == null) {
                log.warn("Nie znaleziono użytkownika: {}", auth.getName());
                return "error";
            }

            Student student = uzytkownik.getStudent();
            if (student == null) {
                log.warn("Użytkownik {} nie ma powiązanego studenta", auth.getName());
                model.addAttribute("plan", List.of());
                return "student/plan";
            }

            // 🔥 Konwersja Long → Integer
            List<PozycjaPlanu> plan = planService.getPlanForStudent(student.getId().intValue());
            model.addAttribute("plan", plan);
            model.addAttribute("username", auth.getName());

            log.info("Wyświetlono plan dla studenta ID: {}", student.getId());
            return "student/plan";

        } catch (Exception e) {
            log.error("Błąd podczas pobierania planu dla studenta: {}", e.getMessage(), e);
            model.addAttribute("error", "Nie udało się pobrać planu zajęć");
            return "error";
        }
    }
}