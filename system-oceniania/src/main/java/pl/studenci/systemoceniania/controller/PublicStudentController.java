package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.StatystykiService;

import java.util.List;

@Controller
@RequestMapping("/public/student")
public class PublicStudentController {

    private static final Logger log = LoggerFactory.getLogger(PublicStudentController.class);

    private static final String PROFIL_NIEDOSTEPNY_VIEW = "student/profil_niedostepny";

    private final StudentService studentService;
    private final StatystykiService statystykiService;

    public PublicStudentController(StudentService studentService,
                                   StatystykiService statystykiService) {
        this.studentService = studentService;
        this.statystykiService = statystykiService;
    }
    @GetMapping("/{token}")
    public String pokazOceny(@PathVariable String token, Model model) {

        if (token == null || token.isBlank()) {
            log.warn("Próba dostępu z pustym tokenem");
            return PROFIL_NIEDOSTEPNY_VIEW;
        }

        try {
            Student student = studentService.findBySecureTokenWithOceny(token);

            if (student == null) {
                log.warn("Nie znaleziono studenta dla tokena: {}", maskToken(token));
                return PROFIL_NIEDOSTEPNY_VIEW;
            }
            List<Ocena> oceny = student.getZapisy().stream()
                    .flatMap(z -> z.getOceny().stream())
                    .toList();

            Double srednia = statystykiService
                    .pobierzSredniaStudenta(student.getId())
                    .orElse(null);

            model.addAttribute("student", student);
            model.addAttribute("oceny", oceny);
            model.addAttribute("srednia", srednia);

            log.info("Wyświetlono profil publiczny studenta ID: {}", student.getId());
            return "student/profil_publiczny";

        } catch (Exception e) {
            log.error("Błąd podczas wyświetlania profilu publicznego dla tokena {}: {}",
                    maskToken(token), e.getMessage());
            return PROFIL_NIEDOSTEPNY_VIEW;
        }
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 6) return "***";
        int prefixLen = Math.min(3, token.length() / 2);
        int suffixLen = Math.min(3, token.length() / 2);
        return token.substring(0, prefixLen) + "..." + token.substring(token.length() - suffixLen);
    }
}