package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Kierunek;
import pl.studenci.systemoceniania.repository.KierunekRepository;

@Controller
@RequestMapping("/kierunki")
public class KierunekController {
    private final KierunekRepository repo;
    public KierunekController(KierunekRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(Model model) { model.addAttribute("kierunki", repo.findAll()); return "kierunki/lista"; }
    @GetMapping("/nowy")
    public String form(Model model) { model.addAttribute("kierunek", new Kierunek()); return "kierunki/formularz"; }
    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Long id, Model model) { model.addAttribute("kierunek", repo.findById(id).orElseThrow()); return "kierunki/formularz"; }
    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("kierunek") Kierunek k, BindingResult br) {
        if (br.hasErrors()) return "kierunki/formularz";
        repo.save(k);
        return "redirect:/kierunki";
    }
    @GetMapping("/usun/{id}")
    public String delete(@PathVariable Long id) { repo.deleteById(id); return "redirect:/kierunki"; }
}