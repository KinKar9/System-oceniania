package pl.studenci.systemoceniania.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.StatystykiService;

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

        Double srednia = statystykiService.pobierzSredniaStudenta(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("oceny", student.getOceny());
        model.addAttribute("srednia", srednia);

        return "student/profil_publiczny";
    }
}