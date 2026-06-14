package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.studenci.systemoceniania.entity.Sala;
import pl.studenci.systemoceniania.service.SalaService;

@Controller
@RequestMapping("/sale")
public class SalaController {

    private final SalaService service;

    public SalaController(SalaService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sale", service.findAll());
        return "sale/lista";
    }

    @GetMapping("/nowy")
    public String form(Model model) {
        model.addAttribute("sala", new Sala());
        return "sale/formularz";
    }

    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Walidacja ID
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID sali.");
            return "redirect:/sale";
        }
        try {
            Sala sala = service.findById(id);
            model.addAttribute("sala", sala);
            return "sale/formularz";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Sala o podanym ID nie istnieje.");
            return "redirect:/sale";
        }
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sale/formularz";
        }
        try {
            service.save(sala);
            redirectAttributes.addFlashAttribute("success", "Sala została zapisana pomyślnie.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisywania sali: " + e.getMessage());
            return "sale/formularz";
        }
        return "redirect:/sale";
    }

    @PostMapping("/usun/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Walidacja ID
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID sali.");
            return "redirect:/sale";
        }
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Sala została usunięta pomyślnie.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nie można usunąć sali: " + e.getMessage());
        }
        return "redirect:/sale";
    }
}