package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.service.PracownikService;

@Controller
@RequestMapping("/pracownicy")
public class PracownikController {

    private static final Logger log = LoggerFactory.getLogger(PracownikController.class);
    private final PracownikService service;

    public PracownikController(PracownikService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pracownicy", service.findAll());
        return "pracownicy/lista";
    }

    @GetMapping("/nowy")
    public String form(Model model) {
        model.addAttribute("pracownik", new Pracownik());
        return "pracownicy/formularz";
    }

    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID pracownika.");
            return "redirect:/pracownicy";
        }
        try {
            Pracownik pracownik = service.findById(id);
            model.addAttribute("pracownik", pracownik);
            return "pracownicy/formularz";
        } catch (Exception e) {
            log.warn("Próba edycji nieistniejącego pracownika o ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Pracownik o podanym ID nie istnieje.");
            return "redirect:/pracownicy";
        }
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("pracownik") Pracownik p,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Błędy walidacji przy zapisie pracownika: {}", bindingResult.getAllErrors());
            return "pracownicy/formularz";
        }
        try {
            service.save(p);
            redirectAttributes.addFlashAttribute("success", "Pracownik został zapisany pomyślnie.");
            log.info("Zapisano pracownika: {} {}", p.getImie(), p.getNazwisko());
        } catch (DataIntegrityViolationException e) {
            log.warn("Naruszenie unikalności (np. email) przy zapisie pracownika: {}", e.getMessage());
            bindingResult.rejectValue("email", "error.pracownik", "Podany email już istnieje w systemie.");
            return "pracownicy/formularz";
        } catch (Exception e) {
            log.error("Błąd podczas zapisu pracownika: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisywania pracownika.");
            return "redirect:/pracownicy";
        }
        return "redirect:/pracownicy";
    }

    @PostMapping("/usun/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID pracownika.");
            return "redirect:/pracownicy";
        }
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Pracownik został usunięty pomyślnie.");
            log.info("Usunięto pracownika o ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Nie można usunąć pracownika o ID {} – powiązane grupy", id);
            redirectAttributes.addFlashAttribute("error", "Nie można usunąć pracownika, ponieważ prowadzi grupy lub ma inne powiązania.");
        } catch (Exception e) {
            log.error("Błąd podczas usuwania pracownika {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania pracownika.");
        }
        return "redirect:/pracownicy";
    }


}