package pl.studenci.systemoceniania.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Sala;
import pl.studenci.systemoceniania.service.SalaService;

@Controller
@RequestMapping("/sale")
public class SalaController {
    private final SalaService service;

    public SalaController(SalaService service) { this.service = service; }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sale", service.findAll());
        return "sale/lista";
    }

    @GetMapping("/nowa")
    public String form(Model model) {
        model.addAttribute("sala", new Sala());
        return "sale/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@ModelAttribute("sala") Sala s) {
        service.save(s);
        return "redirect:/sale";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/sale";
    }
}