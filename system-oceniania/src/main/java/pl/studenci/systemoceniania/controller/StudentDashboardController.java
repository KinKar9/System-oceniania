package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.service.OcenaService;
import pl.studenci.systemoceniania.service.StatystykiService;
import pl.studenci.systemoceniania.service.UzytkownikService;

import java.util.List;

@Controller
@RequestMapping("/student")
@PreAuthorize("isAuthenticated()")
public class StudentDashboardController {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardController.class);

    private final OcenaService ocenaService;
    private final StatystykiService statystykiService;
    private final UzytkownikService uzytkownikService;

    public StudentDashboardController(OcenaService ocenaService,
                                      StatystykiService statystykiService,
                                      UzytkownikService uzytkownikService) {
        this.ocenaService = ocenaService;
        this.statystykiService = statystykiService;
        this.uzytkownikService = uzytkownikService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        model.addAttribute("username", auth.getName());
        return "student/dashboard";
    }

    @GetMapping("/moje-oceny")
    public String myGrades(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        try {
            Student student = pobierzStudentaZAuth(auth);
            if (student == null) return "error";

            List<Ocena> oceny = ocenaService.getOcenyStudenta(student.getId());
            Double srednia = statystykiService
                    .pobierzSredniaStudenta(student.getId())
                    .orElse(null);

            model.addAttribute("student", student);
            model.addAttribute("oceny", oceny);
            model.addAttribute("srednia", srednia);
            return "student/oceny";

        } catch (Exception e) {
            log.error("Błąd podczas pobierania ocen dla użytkownika {}: {}",
                    auth.getName(), e.getMessage(), e);
            return "error";
        }
    }

    @GetMapping("/profil")
    public String profil(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        try {
            Student student = pobierzStudentaZAuth(auth);
            if (student == null) return "error";

            model.addAttribute("student", student);
            return "student/profil";

        } catch (Exception e) {
            log.error("Błąd podczas pobierania profilu dla użytkownika {}: {}",
                    auth.getName(), e.getMessage(), e);
            return "error";
        }
    }

    private Student pobierzStudentaZAuth(Authentication auth) {
        Uzytkownik uzytkownik = uzytkownikService.findByUsername(auth.getName());
        Student student = uzytkownik.getStudent();
        if (student == null) {
            log.warn("Użytkownik '{}' nie ma powiązanego studenta", auth.getName());
        }
        return student;
    }
}