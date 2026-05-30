package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    public String edycja(@PathVariable Long id, Model model) {
        model.addAttribute("sala", service.findById(id));
        return "sale/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sale/formularz"; // Osoba 2: powrót do formularza w razie błędu walidacji
        }
        service.save(sala);
        return "redirect:/sale";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/sale";
    }
}