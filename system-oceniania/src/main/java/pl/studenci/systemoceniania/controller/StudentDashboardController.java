package pl.studenci.systemoceniania.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.service.OcenaService;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.StatystykiService;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {

    private final StudentService studentService;
    private final StatystykiService statystykiService;
    private final OcenaService ocenaService;  // <-- dodane

    public StudentDashboardController(StudentService studentService,
                                      StatystykiService statystykiService,
                                      OcenaService ocenaService) {  // <-- dodany parametr
        this.studentService = studentService;
        this.statystykiService = statystykiService;
        this.ocenaService = ocenaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        return "student/dashboard";
    }

    @GetMapping("/moje-oceny")
    @Transactional   // <-- KLUCZOWE
    public String mojeOceny(Authentication auth, Model model) {
        try {
            String email = auth.getName();
            Student student = studentService.findByEmail(email);
            // Zamiast iterować po leniwej kolekcji, używamy dedykowanej metody z JOIN FETCH
            List<Ocena> oceny = ocenaService.getOcenyStudenta(student.getId());
            Double srednia = statystykiService.pobierzSredniaStudenta(student.getId());
            model.addAttribute("student", student);
            model.addAttribute("oceny", oceny);
            model.addAttribute("srednia", srednia);
            return "student/oceny";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/profil")
    @Transactional   // już było, ale zostawiamy
    public String profil(Authentication auth, Model model) {
        String email = auth.getName();
        Student student = studentService.findByEmail(email);
        model.addAttribute("student", student);
        return "student/profil";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}