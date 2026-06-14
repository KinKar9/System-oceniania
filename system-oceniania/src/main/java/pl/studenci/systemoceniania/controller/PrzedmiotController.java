package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.studenci.systemoceniania.entity.Przedmiot;
import pl.studenci.systemoceniania.service.PrzedmiotService;

@Controller
@RequestMapping("/przedmioty")
public class PrzedmiotController {

    private static final Logger log = LoggerFactory.getLogger(PrzedmiotController.class);
    private final PrzedmiotService service;

    public PrzedmiotController(PrzedmiotService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        log.debug("Pobieranie listy wszystkich przedmiotów");
        model.addAttribute("przedmioty", service.findAll());
        return "przedmioty/lista";
    }

    @GetMapping("/nowy")
    public String form(Model model) {
        model.addAttribute("przedmiot", new Przedmiot());
        return "przedmioty/formularz";
    }

    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Walidacja ID
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID przedmiotu.");
            return "redirect:/przedmioty";
        }
        try {
            Przedmiot przedmiot = service.findById(id);
            model.addAttribute("przedmiot", przedmiot);
            return "przedmioty/formularz";
        } catch (Exception e) {
            log.warn("Próba edycji nieistniejącego przedmiotu o ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Przedmiot o podanym ID nie istnieje.");
            return "redirect:/przedmioty";
        }
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("przedmiot") Przedmiot przedmiot,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "przedmioty/formularz";
        }
        try {
            service.save(przedmiot);
            redirectAttributes.addFlashAttribute("success", "Przedmiot został zapisany pomyślnie.");
            log.info("Zapisano przedmiot: {}", przedmiot.getNazwa());
        } catch (Exception e) {
            log.error("Błąd podczas zapisywania przedmiotu: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisywania przedmiotu.");
            return "przedmioty/formularz";
        }
        return "redirect:/przedmioty";
    }

    @PostMapping("/usun/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID przedmiotu.");
            return "redirect:/przedmioty";
        }
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Przedmiot został usunięty pomyślnie.");
            log.info("Usunięto przedmiot o ID: {}", id);
        } catch (Exception e) {
            log.warn("Nie można usunąć przedmiotu o ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Nie można usunąć przedmiotu – może być powiązany z grupami lub ocenami.");
        }
        return "redirect:/przedmioty";
    }
}