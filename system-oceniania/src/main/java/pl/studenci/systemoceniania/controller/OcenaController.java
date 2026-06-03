package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Zapisy;
import pl.studenci.systemoceniania.service.OcenaService;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.PrzedmiotService;
import pl.studenci.systemoceniania.service.ZapisyService;
import pl.studenci.systemoceniania.repository.SlownikOcenRepository;

import java.util.List;

@Controller
@RequestMapping("/oceny")
public class OcenaController {

    private final OcenaService ocenaService;
    private final StudentService studentService;
    private final PrzedmiotService przedmiotService;
    private final SlownikOcenRepository slownikOcenRepository;
    private final ZapisyService zapisyService;

    public OcenaController(OcenaService ocenaService,
                           StudentService studentService,
                           PrzedmiotService przedmiotService,
                           SlownikOcenRepository slownikOcenRepository,
                           ZapisyService zapisyService) {
        this.ocenaService = ocenaService;
        this.studentService = studentService;
        this.przedmiotService = przedmiotService;
        this.slownikOcenRepository = slownikOcenRepository;
        this.zapisyService = zapisyService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long studentId,
                       @RequestParam(required = false) Long przedmiotId,
                       @RequestParam(required = false) Long typId,
                       @RequestParam(defaultValue = "data") String sortBy,
                       @RequestParam(defaultValue = "desc") String order,
                       Model model) {
        List<Ocena> oceny = ocenaService.filterAndSort(studentId, przedmiotId, typId, sortBy, order);
        model.addAttribute("oceny", oceny);
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("przedmioty", przedmiotService.findAll());
        model.addAttribute("typyOcen", slownikOcenRepository.findAll());
        return "oceny/lista";
    }

    @GetMapping("/nowa")
    public String form(Model model) {
        Ocena ocena = new Ocena();
        ocena.setZapis(new Zapisy());
        model.addAttribute("ocena", ocena);
        model.addAttribute("wszystkieZapisy", zapisyService.findAll()); // lista do wyboru
        model.addAttribute("typyOcen", slownikOcenRepository.findAll());
        return "oceny/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("ocena") Ocena ocena,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("wszystkieZapisy", zapisyService.findAll());
            model.addAttribute("typyOcen", slownikOcenRepository.findAll());
            return "oceny/formularz";
        }
        Long zapisId = ocena.getZapis().getId();
        Zapisy zapis = zapisyService.findById(zapisId);
        ocena.setZapis(zapis);

        ocenaService.save(ocena);
        return "redirect:/oceny";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        ocenaService.delete(id);
        return "redirect:/oceny";
    }
}