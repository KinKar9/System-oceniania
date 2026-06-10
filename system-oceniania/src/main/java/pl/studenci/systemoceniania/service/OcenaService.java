package pl.studenci.systemoceniania.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.*;
import pl.studenci.systemoceniania.repository.*;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OcenaService {
    private final OcenaRepository ocenaRepository;
    private final ZapisyRepository zapisyRepository;
    private final SlownikOcenRepository slownikOcenRepository;

    public OcenaService(OcenaRepository ocenaRepository,
                        ZapisyRepository zapisyRepository,
                        SlownikOcenRepository slownikOcenRepository) {
        this.ocenaRepository = ocenaRepository;
        this.zapisyRepository = zapisyRepository;
        this.slownikOcenRepository = slownikOcenRepository;
    }

    public List<Ocena> findAll() { return ocenaRepository.findAll(); }

    // NOWA metoda – zwraca oceny studenta z załadowanymi wszystkimi zależnościami
    public List<Ocena> getOcenyStudenta(Long studentId) {
        return ocenaRepository.findOcenyStudentaWithDetails(studentId);
    }

    public Ocena save(Ocena ocena) {
        // Walidacja zakresu i kroku 0.5
        if (ocena.getWartosc() < 2.0 || ocena.getWartosc() > 5.0 || (ocena.getWartosc() * 2) % 1 != 0) {
            throw new IllegalArgumentException("Ocena musi być w zakresie 2.0-5.0 co 0.5");
        }
        Zapisy zapis = zapisyRepository.findById(ocena.getZapis().getId())
                .orElseThrow(() -> new IllegalArgumentException("Zapis nie istnieje"));
        if (!"Aktywny".equals(zapis.getStatus())) {
            throw new IllegalArgumentException("Student nie jest aktywny w tym zapisie");
        }
        SlownikOcen typ = slownikOcenRepository.findById(ocena.getTyp().getId())
                .orElseThrow(() -> new IllegalArgumentException("Typ oceny nie istnieje"));

        if (ocenaRepository.existsByZapisIdAndTypId(zapis.getId(), typ.getId())) {
            throw new IllegalArgumentException("Ocena tego typu już została wystawiona dla tego zapisu");
        }

        if (ocena.getDataWystawienia() != null && ocena.getDataWystawienia().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data wystawienia nie może być z przyszłości");
        }
        return ocenaRepository.save(ocena);
    }

    public void delete(Long id) { ocenaRepository.deleteById(id); }

    public List<Ocena> filterAndSort(Long studentId, Long przedmiotId, Long typId, String sortBy, String order) {
        if (sortBy == null || sortBy.isEmpty()) sortBy = "data";
        if (order == null || order.isEmpty()) order = "desc";
        return ocenaRepository.filterAndSort(studentId, przedmiotId, typId, sortBy, order);
    }

    public Ocena findById(Long id) {
        return ocenaRepository.findById(id).orElse(null);
    }
}