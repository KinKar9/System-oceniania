package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Przedmiot;
import pl.studenci.systemoceniania.service.PrzedmiotService;

@Controller
@RequestMapping("/przedmioty")
public class PrzedmiotController {

    private final PrzedmiotService service;

    public PrzedmiotController(PrzedmiotService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("przedmioty", service.findAll());
        return "przedmioty/lista";
    }

    @GetMapping("/nowy")
    public String form(Model model) {
        model.addAttribute("przedmiot", new Przedmiot());
        return "przedmioty/formularz";
    }

    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Long id, Model model) {
        model.addAttribute("przedmiot", service.findById(id));
        return "przedmioty/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("przedmiot") Przedmiot przedmiot, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "przedmioty/formularz"; // Osoba 2: powrót do formularza w razie błędu walidacji
        }
        service.save(przedmiot);
        return "redirect:/przedmioty";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/przedmioty";
    }
}