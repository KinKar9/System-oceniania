package pl.studenci.systemoceniania.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.studenci.systemoceniania.entity.Kierunek;
import pl.studenci.systemoceniania.repository.KierunekRepository;

@Controller
@RequestMapping("/kierunki")
public class KierunekController {

    private static final Logger log = LoggerFactory.getLogger(KierunekController.class);
    private final KierunekRepository repo;

    public KierunekController(KierunekRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("kierunki", repo.findAll());
        return "kierunki/lista";
    }

    @GetMapping("/nowy")
    public String form(Model model) {
        model.addAttribute("kierunek", new Kierunek());
        return "kierunki/formularz";
    }

    @GetMapping("/edycja/{id}")
    public String edycja(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID kierunku.");
            return "redirect:/kierunki";
        }
        return repo.findById(id)
                .map(kierunek -> {
                    model.addAttribute("kierunek", kierunek);
                    return "kierunki/formularz";
                })
                .orElseGet(() -> {
                    log.warn("Próba edycji nieistniejącego kierunku o ID: {}", id);
                    redirectAttributes.addFlashAttribute("error", "Kierunek o podanym ID nie istnieje.");
                    return "redirect:/kierunki";
                });
    }

    @PostMapping("/zapisz")
    public String save(@Valid @ModelAttribute("kierunek") Kierunek k,
                       BindingResult br,
                       RedirectAttributes redirectAttributes) {
        if (br.hasErrors()) {
            return "kierunki/formularz";
        }
        try {
            repo.save(k);
            redirectAttributes.addFlashAttribute("success", "Kierunek został zapisany pomyślnie.");
            log.info("Zapisano kierunek: {}", k.getNazwa());
        } catch (DataIntegrityViolationException e) {
            log.warn("Naruszenie unikalności przy zapisie kierunku: {}", e.getMessage());
            br.rejectValue("nazwa", "error.kierunek", "Kierunek o tej nazwie już istnieje");
            return "kierunki/formularz";
        } catch (Exception e) {
            log.error("Błąd podczas zapisu kierunku: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas zapisywania kierunku.");
            return "redirect:/kierunki";
        }
        return "redirect:/kierunki";
    }

    @PostMapping("/usun/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (id == null || id <= 0) {
            redirectAttributes.addFlashAttribute("error", "Nieprawidłowe ID kierunku.");
            return "redirect:/kierunki";
        }
        try {
            repo.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Kierunek został usunięty pomyślnie.");
            log.info("Usunięto kierunek o ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Nie można usunąć kierunku o ID {} – powiązane rekordy", id);
            redirectAttributes.addFlashAttribute("error", "Nie można usunąć kierunku, ponieważ są do niego przypisane przedmioty lub inne dane.");
        } catch (Exception e) {
            log.error("Błąd podczas usuwania kierunku {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania kierunku.");
        }
        return "redirect:/kierunki";
    }
}