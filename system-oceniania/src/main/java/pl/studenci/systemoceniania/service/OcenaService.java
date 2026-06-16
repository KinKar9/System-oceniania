package pl.studenci.systemoceniania.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.SlownikOcen;
import pl.studenci.systemoceniania.entity.Zapisy;
import pl.studenci.systemoceniania.repository.OcenaRepository;
import pl.studenci.systemoceniania.repository.SlownikOcenRepository;
import pl.studenci.systemoceniania.repository.ZapisyRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OcenaService {

    private static final Logger log = LoggerFactory.getLogger(OcenaService.class);
    private static final double MIN_OCENA = 2.0;
    private static final double MAX_OCENA = 5.0;
    private static final double KROK = 0.5;

    private final OcenaRepository ocenaRepository;
    private final ZapisyRepository zapisyRepository;
    private final SlownikOcenRepository slownikOcenRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public OcenaService(OcenaRepository ocenaRepository,
                        ZapisyRepository zapisyRepository,
                        SlownikOcenRepository slownikOcenRepository) {
        this.ocenaRepository = ocenaRepository;
        this.zapisyRepository = zapisyRepository;
        this.slownikOcenRepository = slownikOcenRepository;
    }

    @Transactional(readOnly = true)
    public List<Ocena> findAll() {
        return ocenaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Ocena> findAllSortedByPopularnoscPrzedmiotu() {
        return ocenaRepository.findAllSortedByPopularnoscPrzedmiotu();
    }

    @Transactional(readOnly = true)
    public List<Ocena> getOcenyStudenta(Long studentId) {
        if (studentId == null || studentId <= 0) {
            log.warn("Próba pobrania ocen z nieprawidłowym studentId: {}", studentId);
            throw new IllegalArgumentException("Nieprawidłowy identyfikator studenta");
        }
        try {
            return ocenaRepository.findOcenyStudentaWithDetails(studentId);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ocen studenta {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Nie udało się pobrać ocen", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Ocena> filterAndSort(Long studentId, Long przedmiotId, Long typId, String sortBy, String order) {
        return filterAndSort(studentId, przedmiotId, typId, null, sortBy, order);
    }

    @Transactional(readOnly = true)
    public List<Ocena> filterAndSort(Long studentId, Long przedmiotId, Long typId,
                                     LocalDate dataOd, String sortBy, String order) {
        if (sortBy == null || sortBy.isEmpty()) sortBy = "data";
        if (order == null || order.isEmpty()) order = "desc";

        // Budowa dynamicznego zapytania JPQL
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT o FROM Ocena o " +
                        "JOIN FETCH o.zapis z " +
                        "JOIN FETCH z.student s " +
                        "JOIN FETCH z.grupa g " +
                        "JOIN FETCH g.przedmiot p " +
                        "JOIN FETCH o.typ t " +
                        "WHERE 1=1"
        );

        if (studentId != null) {
            jpql.append(" AND s.id = :studentId");
        }
        if (przedmiotId != null) {
            jpql.append(" AND p.id = :przedmiotId");
        }
        if (typId != null) {
            jpql.append(" AND t.id = :typId");
        }
        if (dataOd != null) {
            jpql.append(" AND o.dataWystawienia >= :dataOd");
        }

        // Mapowanie nazwy sortowania na ścieżkę w encji
        String orderField;
        switch (sortBy) {
            case "wartosc":
                orderField = "o.wartosc";
                break;
            case "data":
                orderField = "o.dataWystawienia";
                break;
            case "nazwisko":
                orderField = "s.nazwisko";
                break;
            default:
                orderField = "o.dataWystawienia";
        }
        jpql.append(" ORDER BY ").append(orderField).append(" ").append(order.equalsIgnoreCase("asc") ? "ASC" : "DESC");

        TypedQuery<Ocena> query = entityManager.createQuery(jpql.toString(), Ocena.class);

        if (studentId != null) {
            query.setParameter("studentId", studentId);
        }
        if (przedmiotId != null) {
            query.setParameter("przedmiotId", przedmiotId);
        }
        if (typId != null) {
            query.setParameter("typId", typId);
        }
        if (dataOd != null) {
            query.setParameter("dataOd", dataOd);
        }

        try {
            return query.getResultList();
        } catch (Exception e) {
            log.error("Błąd podczas filtrowania i sortowania ocen: {}", e.getMessage(), e);
            throw new RuntimeException("Nie udało się przefiltrować ocen", e);
        }
    }

    @Transactional
    public Ocena save(Ocena ocena) {
        // ... (bez zmian – zachowaj dotychczasową implementację)
        if (ocena == null) {
            log.error("Próba zapisu null jako ocena");
            throw new IllegalArgumentException("Ocena nie może być nullem");
        }
        if (ocena.getZapis() == null || ocena.getZapis().getId() == null) {
            log.warn("Próba zapisu oceny bez przypisanego zapisu");
            throw new IllegalArgumentException("Zapis musi być określony");
        }
        if (ocena.getTyp() == null || ocena.getTyp().getId() == null) {
            log.warn("Próba zapisu oceny bez przypisanego typu");
            throw new IllegalArgumentException("Typ oceny musi być określony");
        }

        double wartosc = ocena.getWartosc();
        if (!isValidGrade(wartosc)) {
            log.warn("Nieprawidłowa wartość oceny: {}", wartosc);
            throw new IllegalArgumentException("Ocena musi być w zakresie " + MIN_OCENA + "-" + MAX_OCENA + " co " + KROK);
        }

        Zapisy zapis = zapisyRepository.findById(ocena.getZapis().getId())
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono zapisu o ID: {}", ocena.getZapis().getId());
                    return new IllegalArgumentException("Zapis nie istnieje");
                });

        entityManager.refresh(zapis);

        if (zapis.getStatus() != Zapisy.StatusZapisu.AKTYWNY) {
            log.warn("Próba wystawienia oceny dla nieaktywnego zapisu ID: {} (status: {})", zapis.getId(), zapis.getStatus());
            throw new IllegalArgumentException("Student nie jest aktywny w tym zapisie");
        }

        SlownikOcen typ = slownikOcenRepository.findById(ocena.getTyp().getId())
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono typu oceny o ID: {}", ocena.getTyp().getId());
                    return new IllegalArgumentException("Typ oceny nie istnieje");
                });

        if (ocenaRepository.existsByZapisIdAndTypId(zapis.getId(), typ.getId())) {
            log.warn("Próba duplikacji oceny: zapis ID {}, typ ID {}", zapis.getId(), typ.getId());
            throw new IllegalArgumentException("Ocena tego typu już została wystawiona dla tego zapisu");
        }

        if (ocena.getDataWystawienia() != null && ocena.getDataWystawienia().isAfter(LocalDate.now())) {
            log.warn("Data wystawienia oceny z przyszłości: {}", ocena.getDataWystawienia());
            throw new IllegalArgumentException("Data wystawienia nie może być z przyszłości");
        }

        try {
            Ocena saved = ocenaRepository.save(ocena);
            log.info("Zapisano ocenę ID: {} dla zapisu ID: {}", saved.getId(), zapis.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("Naruszenie integralności przy zapisie oceny: {}", e.getMessage());
            throw new IllegalArgumentException("Nieprawidłowe dane – możliwy konflikt unikalności", e);
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas zapisu oceny: {}", e.getMessage(), e);
            throw new RuntimeException("Nie udało się zapisać oceny", e);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba usunięcia oceny z nieprawidłowym ID: {}", id);
            throw new IllegalArgumentException("Nieprawidłowe ID oceny");
        }
        if (!ocenaRepository.existsById(id)) {
            log.warn("Próba usunięcia nieistniejącej oceny o ID: {}", id);
            throw new IllegalArgumentException("Ocena o podanym ID nie istnieje");
        }
        try {
            ocenaRepository.deleteById(id);
            log.info("Usunięto ocenę o ID: {}", id);
        } catch (Exception e) {
            log.error("Błąd podczas usuwania oceny {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Nie udało się usunąć oceny", e);
        }
    }

    @Transactional(readOnly = true)
    public Ocena findById(Long id) {
        return ocenaRepository.findById(id).orElse(null);
    }

    private boolean isValidGrade(double value) {
        if (value < MIN_OCENA || value > MAX_OCENA) return false;
        double scaled = value / KROK;
        double remainder = scaled - Math.floor(scaled);
        return Math.abs(remainder) < 1e-9 || Math.abs(remainder - 1.0) < 1e-9;
    }
}