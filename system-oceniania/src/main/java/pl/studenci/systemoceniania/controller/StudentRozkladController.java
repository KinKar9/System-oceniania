package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.studenci.systemoceniania.entity.PozycjaPlanu;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.service.PlanZajecService;
import pl.studenci.systemoceniania.service.UzytkownikService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentRozkladController {

    private static final Logger log = LoggerFactory.getLogger(StudentRozkladController.class);

    private final PlanZajecService planService;
    private final UzytkownikService uzytkownikService;

    public StudentRozkladController(PlanZajecService planService,
                                    UzytkownikService uzytkownikService) {
        this.planService = planService;
        this.uzytkownikService = uzytkownikService;
    }

    @GetMapping("/rozklad")
    public String showRozklad(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        try {
            Uzytkownik uzytkownik = uzytkownikService.findByUsername(auth.getName());
            if (uzytkownik == null) {
                log.warn("Nie znaleziono użytkownika: {}", auth.getName());
                return "error";
            }

            Student student = uzytkownik.getStudent();
            if (student == null) {
                log.warn("Użytkownik {} nie ma powiązanego studenta", auth.getName());
                model.addAttribute("plan", List.of());
                model.addAttribute("godziny", getGodziny());
                model.addAttribute("dni", getDni());
                model.addAttribute("zajeciaMap", new HashMap<>());
                return "student/rozklad";
            }

            // 🔥 KONWERSJA: Long → Integer
            List<PozycjaPlanu> plan = planService.getPlanForStudent(student.getId().intValue());

            Map<String, Object> zajeciaMap = buildScheduleMap(plan);

            model.addAttribute("plan", plan);
            model.addAttribute("godziny", getGodziny());
            model.addAttribute("dni", getDni());
            model.addAttribute("zajeciaMap", zajeciaMap);
            model.addAttribute("username", auth.getName());

            log.info("Wyświetlono rozkład dla studenta ID: {}", student.getId());
            return "student/rozklad";

        } catch (Exception e) {
            log.error("Błąd podczas pobierania rozkładu: {}", e.getMessage(), e);
            return "error";
        }
    }

    private List<String> getGodziny() {
        return IntStream.range(8, 20)
                .mapToObj(h -> String.format("%02d:00", h))
                .collect(Collectors.toList());
    }

    private List<String> getDni() {
        return Arrays.asList("PONIEDZIALEK", "WTOREK", "SRODA", "CZWARTEK", "PIATEK");
    }

    private Map<String, Object> buildScheduleMap(List<PozycjaPlanu> plan) {
        Map<String, Object> map = new HashMap<>();

        for (PozycjaPlanu p : plan) {
            String dzien = p.getDzienTygodnia().name();
            String godzina = p.getGodzinaRozpoczecia().toString().substring(0, 5);

            map.put(dzien + "_" + godzina + "_przedmiot", p.getPrzedmiot().getNazwa());
            map.put(dzien + "_" + godzina + "_sala", p.getSala().getNumerSali());
            map.put(dzien + "_" + godzina + "_prowadzacy", p.getProwadzacy().getUsername());

            List<String> zajeciaList = (List<String>) map.getOrDefault(dzien, new ArrayList<>());
            if (!zajeciaList.contains(godzina)) {
                zajeciaList.add(godzina);
                map.put(dzien, zajeciaList);
            }
        }

        return map;
    }
}