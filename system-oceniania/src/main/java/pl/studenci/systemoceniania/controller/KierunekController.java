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
        model.addAttribute("kierunki", repo.findAll()); // tylko nieusunięte
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
                    log.warn("Próba edycji nieistniejącego lub usuniętego kierunku o ID: {}", id);
                    redirectAttributes.addFlashAttribute("error", "Kierunek o podanym ID nie istnieje lub został usunięty.");
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
            if (k.getId() != null && repo.existsById(k.getId())) {
                Kierunek existing = repo.findById(k.getId()).get();
                existing.setNazwa(k.getNazwa());
                existing.setKodKierunku(k.getKodKierunku());
                existing.setStopien(k.getStopien());
                repo.save(existing);
                log.info("Zaktualizowano kierunek: {}", existing.getNazwa());
            } else {
                k.setDeleted(false);
                repo.save(k);
                log.info("Zapisano nowy kierunek: {}", k.getNazwa());
            }
            redirectAttributes.addFlashAttribute("success", "Kierunek został zapisany pomyślnie.");
        } catch (DataIntegrityViolationException e) {
            log.warn("Naruszenie unikalności przy zapisie kierunku: {}", e.getMessage());
            br.rejectValue("nazwa", "error.kierunek", "Kierunek o tej nazwie już istnieje");
            return "kierunki/formularz";
        } catch (Exception e) {
            log.error("Błąd podczas zapisu kierunku: {}", e.getMessage(), e);
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

        Kierunek kierunek = repo.findById(id).orElse(null);
        if (kierunek == null) {
            redirectAttributes.addFlashAttribute("error", "Kierunek o podanym ID nie istnieje lub został już usunięty.");
            return "redirect:/kierunki";
        }

        try {
            kierunek.setDeleted(true);
            repo.save(kierunek);
            redirectAttributes.addFlashAttribute("success", "Kierunek został oznaczony jako usunięty. Dane pozostają w bazie, ale nie są wyświetlane.");
            log.info("Miękko usunięto kierunek o ID: {}", id);
        } catch (Exception e) {
            log.error("Błąd podczas miękkiego usuwania kierunku {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Wystąpił błąd podczas usuwania kierunku.");
        }
        return "redirect:/kierunki";
    }

    @PostMapping("/przywroc/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Kierunek kierunek = repo.findDeletedById(id).orElse(null);
        if (kierunek == null) {
            redirectAttributes.addFlashAttribute("error", "Nie znaleziono usuniętego kierunku o podanym ID.");
            return "redirect:/kierunki";
        }
        kierunek.setDeleted(false);
        repo.save(kierunek);
        redirectAttributes.addFlashAttribute("success", "Kierunek został przywrócony.");
        return "redirect:/kierunki";
    }
}