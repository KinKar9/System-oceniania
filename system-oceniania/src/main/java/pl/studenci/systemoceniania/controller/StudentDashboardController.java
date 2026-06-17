package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.service.OcenaService;
import pl.studenci.systemoceniania.service.StatystykiService;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.UzytkownikService;

import java.util.List;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentDashboardController {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardController.class);

    private final OcenaService ocenaService;
    private final StatystykiService statystykiService;
    private final UzytkownikService uzytkownikService;
    private final StudentService studentService;

    public StudentDashboardController(OcenaService ocenaService,
                                      StatystykiService statystykiService,
                                      UzytkownikService uzytkownikService,
                                      StudentService studentService) {
        this.ocenaService = ocenaService;
        this.statystykiService = statystykiService;
        this.uzytkownikService = uzytkownikService;
        this.studentService = studentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        model.addAttribute("username", auth.getName());

        try {
            Student student = pobierzStudentaZAuth(auth);
            if (student != null) {
                model.addAttribute("student", student);
                if (student.getSecureToken() != null) {
                    String link = "http://localhost:8080/public/student/" + student.getSecureToken();
                    model.addAttribute("publicLink", link);
                }
            }
        } catch (Exception e) {
            log.warn("Nie udało się pobrać studenta dla dashboardu: {}", e.getMessage());
        }

        return "student/dashboard";
    }

    @GetMapping("/moje-oceny")
    public String myGrades(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        try {
            Student student = pobierzStudentaZAuth(auth);
            if (student == null) return "error";

            List<Ocena> oceny = ocenaService.getOcenyStudenta(student.getId());

            Double srednia = null;
            if (!oceny.isEmpty()) {
                double sumaIloczynow = 0.0;
                double sumaWag = 0.0;
                for (Ocena o : oceny) {
                    double waga = o.getTyp().getWaga().doubleValue();
                    sumaIloczynow += o.getWartosc() * waga;
                    sumaWag += waga;
                }
                if (sumaWag > 0) {
                    srednia = Math.round((sumaIloczynow / sumaWag) * 100.0) / 100.0;
                }
            }

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

    // ============================================================
    // 🔗 NOWE ENDPOINTY DLA PUBLICZNEGO LINKU
    // ============================================================

    @PostMapping("/generuj-link")
    @Transactional  // ← DODANO
    public String generatePublicLink(Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Student student = pobierzStudentaZAuth(auth);
            if (student == null) {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono studenta");
                return "redirect:/student/dashboard";
            }

            String token = studentService.generatePublicToken(student.getId());
            String link = "http://localhost:8080/public/student/" + token;

            redirectAttributes.addFlashAttribute("success", "Link publiczny został wygenerowany!");
            redirectAttributes.addFlashAttribute("publicLink", link);
            return "redirect:/student/dashboard";

        } catch (Exception e) {
            log.error("Błąd generowania linku: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Błąd generowania linku: " + e.getMessage());
            return "redirect:/student/dashboard";
        }
    }

    @PostMapping("/dezaktywuj-link")
    @Transactional  // ← DODANO
    public String deactivatePublicLink(Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            Student student = pobierzStudentaZAuth(auth);
            if (student == null) {
                redirectAttributes.addFlashAttribute("error", "Nie znaleziono studenta");
                return "redirect:/student/dashboard";
            }

            log.info("Dezaktywacja linku publicznego dla studenta ID: {}", student.getId());
            studentService.deactivatePublicToken(student.getId());
            redirectAttributes.addFlashAttribute("success", "Link publiczny został dezaktywowany.");
            log.info("Link publiczny dezaktywowany dla studenta ID: {}", student.getId());
            return "redirect:/student/dashboard";

        } catch (Exception e) {
            log.error("Błąd dezaktywacji linku: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Błąd dezaktywacji linku: " + e.getMessage());
            return "redirect:/student/dashboard";
        }
    }

    private Student pobierzStudentaZAuth(Authentication auth) {
        Uzytkownik uzytkownik = uzytkownikService.findByUsername(auth.getName());
        if (uzytkownik == null) {
            log.warn("Nie znaleziono użytkownika o nazwie: {}", auth.getName());
            return null;
        }
        Student student = uzytkownik.getStudent();
        if (student == null) {
            log.warn("Użytkownik '{}' nie ma powiązanego studenta", auth.getName());
        }
        return student;
    }
}