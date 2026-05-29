package pl.studenci.systemoceniania.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.service.PracownikService;

@Controller
@RequestMapping("/pracownicy")
public class PracownikController {
    private final PracownikService service;

    public PracownikController(PracownikService service) { this.service = service; }

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

    @PostMapping("/zapisz")
    public String save(@ModelAttribute("pracownik") Pracownik p) {
        service.save(p);
        return "redirect:/pracownicy";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/pracownicy";
    }
}