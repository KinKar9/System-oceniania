package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.*;
import pl.studenci.systemoceniania.enums.DzienTygodnia;
import pl.studenci.systemoceniania.service.*;

import java.util.List;

@Controller
@RequestMapping("/pracownik/plany")
@PreAuthorize("hasRole('PRACOWNIK')")
public class PracownikPlanController {

    private static final Logger log = LoggerFactory.getLogger(PracownikPlanController.class);

    private final PlanZajecService planService;
    private final UzytkownikService uzytkownikService;
    private final SemestrService semestrService;
    private final PrzedmiotService przedmiotService;
    private final SalaService salaService;
    private final GrupaService grupaService;  // ← DODANO

    public PracownikPlanController(PlanZajecService planService,
                                   UzytkownikService uzytkownikService,
                                   SemestrService semestrService,
                                   PrzedmiotService przedmiotService,
                                   SalaService salaService,
                                   GrupaService grupaService) {  // ← DODANO
        this.planService = planService;
        this.uzytkownikService = uzytkownikService;
        this.semestrService = semestrService;
        this.przedmiotService = przedmiotService;
        this.salaService = salaService;
        this.grupaService = grupaService;  // ← DODANO
    }

    @GetMapping
    public String listPlans(Authentication auth, Model model) {
        Uzytkownik pracownik = uzytkownikService.findByUsername(auth.getName());
        List<PlanZajec> plany = planService.getPlansForPracownik(pracownik.getId().intValue());
        model.addAttribute("plany", plany);
        model.addAttribute("username", auth.getName());
        return "pracownicy/plany";
    }

    @GetMapping("/nowy")
    public String showCreateForm(Model model) {
        model.addAttribute("plan", new PlanZajec());
        model.addAttribute("semestry", semestrService.findAll());
        return "pracownicy/plan-formularz";
    }

    @PostMapping("/zapisz")
    public String savePlan(@ModelAttribute PlanZajec plan, Authentication auth) {
        Uzytkownik pracownik = uzytkownikService.findByUsername(auth.getName());
        plan.setPracownik(pracownik);

        if (plan.getSemestr() != null && plan.getSemestr().getId() != null) {
            Semestr semestr = semestrService.findById(plan.getSemestr().getId());
            plan.setSemestr(semestr);
        } else {
            plan.setSemestr(null);
        }

        planService.createPlan(plan);
        log.info("Pracownik {} utworzył plan: {}", auth.getName(), plan.getNazwa());
        return "redirect:/pracownik/plany";
    }

    @GetMapping("/edycja/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        PlanZajec plan = planService.findPlanById(id);
        model.addAttribute("plan", plan);
        model.addAttribute("semestry", semestrService.findAll());
        return "pracownicy/plan-formularz";
    }

    @PostMapping("/usun/{id}")
    public String deletePlan(@PathVariable Integer id) {
        planService.deletePlan(id);
        return "redirect:/pracownik/plany";
    }

    // ============================================================
    // POZYCJE PLANU
    // ============================================================

    @GetMapping("/pozycje/{planId}")
    public String listPozycje(@PathVariable Integer planId, Model model) {
        PlanZajec plan = planService.findPlanById(planId);
        model.addAttribute("plan", plan);
        model.addAttribute("pozycje", plan.getPozycje());
        return "pracownicy/pozycje-lista";
    }

    @GetMapping("/pozycje/dodaj/{planId}")
    public String showAddPozycjaForm(@PathVariable Integer planId, Model model) {
        PozycjaPlanu pozycja = new PozycjaPlanu();
        pozycja.setPlanZajec(planService.findPlanById(planId));
        model.addAttribute("pozycja", pozycja);
        model.addAttribute("przedmioty", przedmiotService.findAll());
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("prowadzacy", uzytkownikService.findAllPracownicy());
        model.addAttribute("dni", DzienTygodnia.values());
        // 🔥 DODAJ LISTĘ GRUP
        model.addAttribute("grupy", grupaService.findAll()); // jeśli potrzebujesz listy grup
        return "pracownicy/pozycja-formularz";
    }

    @PostMapping("/pozycje/zapisz")
    public String savePozycja(@ModelAttribute PozycjaPlanu pozycja) {
        // 🔥 Pobierz wszystkie encje z bazy
        Przedmiot przedmiot = przedmiotService.findById(pozycja.getPrzedmiot().getId().intValue());
        Sala sala = salaService.findById(pozycja.getSala().getId().intValue());
        Uzytkownik prowadzacy = uzytkownikService.findById(pozycja.getProwadzacy().getId().intValue());

        // 🔥 Obsługa grupy – pobierz jeśli wybrano
        if (pozycja.getGrupa() != null && pozycja.getGrupa().getId() != null) {
            Grupa grupa = grupaService.findById(pozycja.getGrupa().getId().intValue());
            pozycja.setGrupa(grupa);
        } else {
            pozycja.setGrupa(null);
        }

        pozycja.setPrzedmiot(przedmiot);
        pozycja.setSala(sala);
        pozycja.setProwadzacy(prowadzacy);

        planService.addPozycja(pozycja.getPlanZajec().getId().intValue(), pozycja);
        log.info("Zapisano pozycję planu ID: {}", pozycja.getId());
        return "redirect:/pracownik/plany/pozycje/" + pozycja.getPlanZajec().getId();
    }

    @GetMapping("/pozycje/edycja/{id}")
    public String showEditPozycjaForm(@PathVariable Integer id, Model model) {
        PozycjaPlanu pozycja = planService.findPozycjaById(id);
        model.addAttribute("pozycja", pozycja);
        model.addAttribute("przedmioty", przedmiotService.findAll());
        model.addAttribute("sale", salaService.findAll());
        model.addAttribute("prowadzacy", uzytkownikService.findAllPracownicy());
        model.addAttribute("dni", DzienTygodnia.values());
        model.addAttribute("grupy", grupaService.findAll()); // lista grup
        return "pracownicy/pozycja-formularz";
    }

    @PostMapping("/pozycje/usun/{id}")
    public String deletePozycja(@PathVariable Integer id) {
        PozycjaPlanu pozycja = planService.findPozycjaById(id);
        Integer planId = pozycja.getPlanZajec().getId();
        planService.deletePozycja(id);
        log.info("Usunięto pozycję planu ID: {}", id);
        return "redirect:/pracownik/plany/pozycje/" + planId;
    }
}