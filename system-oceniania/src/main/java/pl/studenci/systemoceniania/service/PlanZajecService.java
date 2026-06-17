package pl.studenci.systemoceniania.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.PlanZajec;
import pl.studenci.systemoceniania.entity.PozycjaPlanu;
import pl.studenci.systemoceniania.repository.PlanZajecRepository;
import pl.studenci.systemoceniania.repository.PozycjaPlanuRepository;

import java.util.List;

@Service
@Transactional
public class PlanZajecService {

    private static final Logger log = LoggerFactory.getLogger(PlanZajecService.class);

    private final PlanZajecRepository planZajecRepository;
    private final PozycjaPlanuRepository pozycjaPlanuRepository;

    public PlanZajecService(PlanZajecRepository planZajecRepository,
                            PozycjaPlanuRepository pozycjaPlanuRepository) {
        this.planZajecRepository = planZajecRepository;
        this.pozycjaPlanuRepository = pozycjaPlanuRepository;
    }

    // ============================================================
    // CRUD DLA PLANU GŁÓWNEGO
    // ============================================================

    public PlanZajec createPlan(PlanZajec plan) {
        return planZajecRepository.save(plan);
    }

    public PlanZajec updatePlan(Integer id, PlanZajec updated) {
        // 🔥 BEZ KONWERSJI – repozytorium oczekuje Integer
        PlanZajec existing = findPlanById(id);
        existing.setNazwa(updated.getNazwa());
        existing.setSemestr(updated.getSemestr());
        existing.setAktywny(updated.isAktywny());
        return planZajecRepository.save(existing);
    }

    public void deletePlan(Integer id) {
        // 🔥 BEZ KONWERSJI
        planZajecRepository.deleteById(id);
    }

    public PlanZajec findPlanById(Integer id) {
        // 🔥 BEZ KONWERSJI
        return planZajecRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan nie istnieje"));
    }

    public List<PlanZajec> getPlansForPracownik(Integer pracownikId) {
        // 🔥 UWAGA: findByPracownikId w repozytorium przyjmuje Long (bo pracownik.id to Long)
        // Ale metoda w serwisie przyjmuje Integer – musimy przekonwertować
        return planZajecRepository.findByPracownikId(pracownikId.longValue());
    }

    // ============================================================
    // ZARZĄDZANIE POZYCJAMI
    // ============================================================

    public PozycjaPlanu addPozycja(Integer planId, PozycjaPlanu pozycja) {
        PlanZajec plan = findPlanById(planId);
        validateConflict(pozycja, null);
        plan.addPozycja(pozycja);
        return pozycjaPlanuRepository.save(pozycja);
    }

    public PozycjaPlanu updatePozycja(Integer pozycjaId, PozycjaPlanu updated) {
        // 🔥 PozycjaPlanuRepository przyjmuje Long – konwersja potrzebna
        PozycjaPlanu existing = pozycjaPlanuRepository.findById(pozycjaId.longValue())
                .orElseThrow(() -> new RuntimeException("Pozycja nie istnieje"));
        validateConflict(updated, pozycjaId);
        existing.setPrzedmiot(updated.getPrzedmiot());
        existing.setProwadzacy(updated.getProwadzacy());
        existing.setSala(updated.getSala());
        existing.setGrupa(updated.getGrupa());
        existing.setDzienTygodnia(updated.getDzienTygodnia());
        existing.setGodzinaRozpoczecia(updated.getGodzinaRozpoczecia());
        existing.setGodzinaZakonczenia(updated.getGodzinaZakonczenia());
        return pozycjaPlanuRepository.save(existing);
    }

    public void deletePozycja(Integer pozycjaId) {
        // 🔥 PozycjaPlanuRepository przyjmuje Long
        pozycjaPlanuRepository.deleteById(pozycjaId.longValue());
    }

    public PozycjaPlanu findPozycjaById(Integer id) {
        // 🔥 PozycjaPlanuRepository przyjmuje Long
        return pozycjaPlanuRepository.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Pozycja nie istnieje"));
    }

    // ============================================================
    // WALIDACJA KOLIZJI
    // ============================================================

    private void validateConflict(PozycjaPlanu pozycja, Integer excludeId) {
        Integer exclude = (excludeId != null) ? excludeId : 0;

        boolean salaConflict = pozycjaPlanuRepository.existsConflictForSala(
                pozycja.getSala().getId().longValue(),
                pozycja.getDzienTygodnia(),
                pozycja.getGodzinaRozpoczecia(),
                pozycja.getGodzinaZakonczenia(),
                exclude.longValue()
        );

        if (salaConflict) {
            throw new IllegalArgumentException("Sala jest już zajęta w tym terminie");
        }

        boolean prowadzacyConflict = pozycjaPlanuRepository.existsConflictForProwadzacy(
                pozycja.getProwadzacy().getId().longValue(),
                pozycja.getDzienTygodnia(),
                pozycja.getGodzinaRozpoczecia(),
                pozycja.getGodzinaZakonczenia(),
                exclude.longValue()
        );

        if (prowadzacyConflict) {
            throw new IllegalArgumentException("Prowadzący ma już zajęcia w tym terminie");
        }
    }

    // ============================================================
    // DLA STUDENTA
    // ============================================================

    public List<PozycjaPlanu> getPlanForStudent(Integer studentId) {
        // 🔥 PozycjaPlanuRepository.findPozycjeForStudent przyjmuje Long
        return pozycjaPlanuRepository.findPozycjeForStudent(studentId.longValue());
    }
}