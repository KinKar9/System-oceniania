package pl.studenci.systemoceniania.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @PostMapping("/zapisz")
    public String save(@ModelAttribute("przedmiot") Przedmiot przedmiot) {
        service.save(przedmiot);
        return "redirect:/przedmioty";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/przedmioty";
    }
}