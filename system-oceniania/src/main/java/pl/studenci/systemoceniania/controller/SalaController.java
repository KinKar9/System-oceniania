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

    // 🔥 ZMIENIONO: Long → Integer
    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
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
        System.out.println("🔵 1. Weszło do metody save()");
        System.out.println("🔵 2. Sala: " + sala.getNumerSali());

        if (bindingResult.hasErrors()) {
            System.out.println("🔴 Błędy walidacji: " + bindingResult.getAllErrors());
            return "sale/formularz";
        }
        try {
            System.out.println("🔵 3. Próba zapisu...");
            service.save(sala);
            System.out.println("✅ 4. Zapisano!");
            redirectAttributes.addFlashAttribute("success", "Sala została zapisana pomyślnie.");
        } catch (Exception e) {
            System.out.println("💥 5. BŁĄD: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisywania sali: " + e.getMessage());
            return "sale/formularz";
        }
        return "redirect:/sale";
    }

    // 🔥 ZMIENIONO: Long → Integer
    @PostMapping("/usun/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
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