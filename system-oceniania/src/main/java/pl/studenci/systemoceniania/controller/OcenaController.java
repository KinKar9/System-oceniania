package pl.studenci.systemoceniania.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import pl.studenci.systemoceniania.service.SlownikOcenService;
import pl.studenci.systemoceniania.service.StudentService;
import pl.studenci.systemoceniania.service.PrzedmiotService;
import pl.studenci.systemoceniania.service.ZapisyService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/oceny")
@PreAuthorize("hasAnyRole('PRACOWNIK', 'ADMIN')")
public class OcenaController {

    private static final Logger log = LoggerFactory.getLogger(OcenaController.class);

    private final OcenaService ocenaService;
    private final StudentService studentService;
    private final PrzedmiotService przedmiotService;
    private final ZapisyService zapisyService;
    private final SlownikOcenService slownikOcenService;

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
                       @RequestParam(required = false) LocalDate dataOd,
                       @RequestParam(required = false) String sortBy,
                       @RequestParam(required = false) String order,
                       @CookieValue(name = "ocenySort", required = false) String cookieSort,
                       HttpServletResponse response,
                       Model model) {
        try {
            if (sortBy == null && cookieSort != null) {
                String[] parts = cookieSort.split("\\|");
                sortBy = parts[0];
                order = parts.length > 1 ? parts[1] : "desc";
            }
            if (sortBy == null) sortBy = "data";
            if (order == null) order = "desc";

            Cookie cookie = new Cookie("ocenySort", sortBy + "|" + order);
            cookie.setPath("/oceny");
            cookie.setMaxAge(30 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            List<Ocena> oceny = ocenaService.filterAndSort(studentId, przedmiotId, typId, dataOd, sortBy, order);
            log.info("Znaleziono {} ocen", oceny.size());
            model.addAttribute("oceny", oceny);
            model.addAttribute("students", studentService.findAll());
            model.addAttribute("przedmioty", przedmiotService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll());
        } catch (Exception e) {
            log.error("Błąd podczas pobierania listy ocen: {}", e.getMessage(), e);
            model.addAttribute("error", "Nie udało się pobrać listy ocen.");
        }
        return "oceny/lista";
    }

    @GetMapping("/nowa")
    public String form(Model model) {
        log.info("Wywołano /oceny/nowa");
        Ocena ocena = new Ocena();
        ocena.setZapis(new Zapisy());
        model.addAttribute("ocena", ocena);
        model.addAttribute("wszystkieZapisy", zapisyService.findAllActive());
        model.addAttribute("typyOcen", slownikOcenService.findAll());
        return "oceny/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("ocena") Ocena ocena,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("wszystkieZapisy", zapisyService.findAllActive());
            model.addAttribute("typyOcen", slownikOcenService.findAll());
            return "oceny/formularz";
        }

        if (ocena.getZapis() == null || ocena.getZapis().getId() == null) {
            bindingResult.rejectValue("zapis.id", "error.ocena", "Zapis musi być wybrany.");
            model.addAttribute("wszystkieZapisy", zapisyService.findAllActive());
            model.addAttribute("typyOcen", slownikOcenService.findAll());
            return "oceny/formularz";
        }

        Long zapisId = ocena.getZapis().getId();
        Zapisy zapis;
        try {
            zapis = zapisyService.findById(zapisId).orElseThrow();
        } catch (Exception e) {
            log.warn("Nie znaleziono zapisu o ID: {}", zapisId);
            bindingResult.rejectValue("zapis.id", "error.ocena", "Wybrany zapis nie istnieje.");
            model.addAttribute("wszystkieZapisy", zapisyService.findAllActive());
            model.addAttribute("typyOcen", slownikOcenService.findAll());
            return "oceny/formularz";
        }

        ocena.setZapis(zapis);

        try {
            ocenaService.save(ocena);
            redirectAttributes.addFlashAttribute("success", "Ocena została wystawiona pomyślnie.");
        } catch (IllegalArgumentException e) {
            log.warn("Błąd walidacji przy zapisie oceny: {}", e.getMessage());
            bindingResult.rejectValue("wartosc", "error.ocena", e.getMessage());
            model.addAttribute("wszystkieZapisy", zapisyService.findAllActive());
            model.addAttribute("typyOcen", slownikOcenService.findAll());
            return "oceny/formularz";
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas zapisu oceny: {}", e.getMessage(), e);
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
            log.error("Błąd podczas usuwania oceny {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Nie można usunąć oceny.");
        }
        return "redirect:/oceny";
    }

    @GetMapping("/popularnosc")
    public String listByPopularnosc(Model model) {
        try {
            List<Ocena> oceny = ocenaService.findAllSortedByPopularnoscPrzedmiotu();
            model.addAttribute("oceny", oceny);
            model.addAttribute("students", studentService.findAll());
            model.addAttribute("przedmioty", przedmiotService.findAll());
            model.addAttribute("typyOcen", slownikOcenService.findAll());
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ocen według popularności: {}", e.getMessage(), e);
            model.addAttribute("error", "Nie udało się pobrać listy ocen.");
        }
        return "oceny/lista";
    }
}