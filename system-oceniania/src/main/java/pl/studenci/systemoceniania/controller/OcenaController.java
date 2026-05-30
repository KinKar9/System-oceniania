package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.OcenaCzastkowa;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.PrzedmiotService;
import pl.studenci.systemoceniania.repository.OcenaCzastkowaRepository;

import java.util.List;

@Controller
@RequestMapping("/oceny")
public class OcenaController {

    private final OcenaCzastkowaRepository ocenaRepository;
    private final StudentService studentService;
    private final PrzedmiotService przedmiotService;

    public OcenaController(OcenaCzastkowaRepository ocenaRepository,
                           StudentService studentService,
                           PrzedmiotService przedmiotService) {
        this.ocenaRepository = ocenaRepository;
        this.studentService = studentService;
        this.przedmiotService = przedmiotService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long studentId, Model model) {
        List<OcenaCzastkowa> oceny;
        if (studentId != null) {
            oceny = ocenaRepository.findByStudentId(studentId);
        } else {
            oceny = ocenaRepository.findAll();
        }

        model.addAttribute("oceny", oceny);
        model.addAttribute("students", studentService.findAll());
        return "oceny/lista";
    }

    @GetMapping("/nowa")
    public String form(Model model) {
        model.addAttribute("ocena", new OcenaCzastkowa());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("przedmioty", przedmiotService.findAll());
        return "oceny/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("ocena") OcenaCzastkowa ocena, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.findAll());
            model.addAttribute("przedmioty", przedmiotService.findAll());
            return "oceny/formularz";
        }
        ocenaRepository.save(ocena);
        return "redirect:/oceny";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        ocenaRepository.deleteById(id);
        return "redirect:/oceny";
    }
}