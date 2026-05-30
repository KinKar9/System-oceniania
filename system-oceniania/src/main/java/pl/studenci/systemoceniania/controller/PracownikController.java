package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.service.PracownikService;

@Controller
@RequestMapping("/pracownicy")
public class PracownikController {

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
    public String edycja(@PathVariable Long id, Model model) {
        model.addAttribute("pracownik", service.findById(id));
        return "pracownicy/formularz";
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("pracownik") Pracownik p, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "pracownicy/formularz";
        }
        service.save(p);
        return "redirect:/pracownicy";
    }

    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/pracownicy";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testAplikacji() {
        return "<h1>Hurra! System działa i nie ma błędu 404!</h1>";
    }
}