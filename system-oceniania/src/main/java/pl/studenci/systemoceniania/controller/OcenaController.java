package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Zapisy;
import pl.studenci.systemoceniania.service.OcenaService;
import pl.studenci.systemoceniania.service.SlownikOcenService; // POPRAWKA 2: serwis zamiast repozytorium
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.PrzedmiotService;
import pl.studenci.systemoceniania.service.ZapisyService;

import java.util.List;

// POPRAWKA 1: Kontrola dostępu — tylko PRACOWNIK i ADMIN mogą zarządzać ocenami
@Controller
@RequestMapping("/oceny")
@PreAuthorize("hasAnyRole('PRACOWNIK', 'ADMIN')")
public class OcenaController {

    private static final Logger log = LoggerFactory.getLogger(OcenaController.class);

    private final OcenaService ocenaService;
    private final StudentService studentService;
    private final PrzedmiotService przedmiotService;
    private final ZapisyService zapisyService;
    private final SlownikOcenService slownikOcenService; // POPRAWKA 2: serwis zamiast repozytorium

    public OcenaController(OcenaService ocenaService,
                           StudentService studentService,
                           PrzedmiotService przedmiotService,
                           ZapisyService zapisyService,
                           SlownikOcenService slownikOcenService) {
        this.ocenaService = ocenaService;
        this.studentService = studentService;
        this.przedmiotService = przedmiotService;
        this.zapisyService = zapisyService;
        this.slownikOcenService = slownikOcenService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long studentId,
                       @RequestParam(required = false) Long przedmiotId,
                       @RequestParam(required = false) Long typId,
                       @RequestParam(defaultValue = "data") String sortBy,
                       @RequestParam(defaultValue = "desc") String order,
                       Model model) {
        try {
            List<Ocena> oceny = ocenaService.filterAndSort(studentId, przedmiotId, typId, sortBy, order);
            model.addAttribute("oceny", oceny);
            model.addAttribute("students", studentService.findAll());
            model.addAttribute("przedmioty", przedmiotService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll()); // POPRAWKA 2
        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy ocen: {}", e.getMessage());
            // POPRAWKA 3: ogólny komunikat bez szczegółów wyjątku
            model.addAttribute("error", "Nie udało się pobrać listy ocen.");
        }
        return "oceny/lista";
    }

    @GetMapping("/nowa")
    public String form(Model model) {
        Ocena ocena = new Ocena();
        ocena.setZapis(new Zapisy());
        model.addAttribute("ocena", ocena);
        model.addAttribute("wszystkieZapisy", zapisyService.findAll());
        model.addAttribute("typyOcen", slownikOcenService.findAll()); // POPRAWKA 2
        return "oceny/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("ocena") Ocena ocena,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("wszystkieZapisy", zapisyService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll()); // POPRAWKA 2
            return "oceny/formularz";
        }

        if (ocena.getZapis() == null || ocena.getZapis().getId() == null) {
            bindingResult.rejectValue("zapis.id", "error.ocena", "Zapis musi być wybrany.");
            model.addAttribute("wszystkieZapisy", zapisyService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll()); // POPRAWKA 2
            return "oceny/formularz";
        }

        Long zapisId = ocena.getZapis().getId();
        Zapisy zapis;
        try {
            zapis = zapisyService.findById(zapisId).orElseThrow();
        } catch (Exception e) {
            log.warn("Nie znaleziono zapisu o ID: {}", zapisId);
            bindingResult.rejectValue("zapis.id", "error.ocena", "Wybrany zapis nie istnieje.");
            model.addAttribute("wszystkieZapisy", zapisyService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll()); // POPRAWKA 2
            return "oceny/formularz";
        }

        ocena.setZapis(zapis);

        try {
            ocenaService.save(ocena);
            redirectAttributes.addFlashAttribute("success", "Ocena została wystawiona pomyślnie.");
        } catch (IllegalArgumentException e) {
            log.warn("Błąd walidacji przy zapisie oceny: {}", e.getMessage());
            bindingResult.rejectValue("wartosc", "error.ocena", e.getMessage());
            model.addAttribute("wszystkieZapisy", zapisyService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll()); // POPRAWKA 2
            return "oceny/formularz";
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas zapisu oceny: {}", e.getMessage());
            // POPRAWKA 3: ogólny komunikat, szczegóły tylko w logu
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisywania oceny.");
            return "redirect:/oceny";
        }
        return "redirect:/oceny";
    }

    @PostMapping("/usun/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID oceny.");
            return "redirect:/oceny";
        }
        try {
            ocenaService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Ocena została usunięta pomyślnie.");
            log.info("Usunięto ocenę o ID: {}", id);
        } catch (Exception e) {
            log.error("Błąd podczas usuwania oceny {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Nie można usunąć oceny.");
        }
        return "redirect:/oceny";
    }
}