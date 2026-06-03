package pl.studenci.systemoceniania.controller;

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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/public/student")
public class PublicStudentController {

    private final StudentService studentService;
    private final StatystykiService statystykiService;

    public PublicStudentController(StudentService studentService, StatystykiService statystykiService) {
        this.studentService = studentService;
        this.statystykiService = statystykiService;
    }

    @GetMapping("/{token}")
    public String pokazOceny(@PathVariable String token, Model model) {
        Student student = studentService.findBySecureToken(token);

        List<Ocena> oceny = student.getZapisy().stream()
                .flatMap(z -> z.getOceny().stream())
                .collect(Collectors.toList());

        Double srednia = statystykiService.pobierzSredniaStudenta(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("oceny", oceny);
        model.addAttribute("srednia", srednia);

        return "student/profil_publiczny";
    }
}