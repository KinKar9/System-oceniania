package pl.studenci.systemoceniania.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StatystykiService {

    private static final Logger log = LoggerFactory.getLogger(StatystykiService.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sprawdza i aktualizuje zaliczenie dla wszystkich zapisów danego studenta.
     * Uruchamia procedurę składowaną PKG_OCENY.SPRAWDZ_ZALICZENIE.
     *
     * @param studentId identyfikator studenta (nie może być null)
     * @throws IllegalArgumentException jeśli studentId jest null lub <= 0
     * @throws RuntimeException jeśli wystąpi błąd podczas wywołania procedury
     */
    @Transactional
    public void sprawdzZaliczenie(Long studentId) {
        // Walidacja parametru
        if (studentId == null || studentId <= 0) {
            log.warn("Próba sprawdzenia zaliczenia z nieprawidłowym studentId: {}", studentId);
            throw new IllegalArgumentException("Identyfikator studenta musi być dodatni i niepusty");
        }

        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PKG_OCENY.SPRAWDZ_ZALICZENIE");
            query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
            query.setParameter(1, studentId);
            query.execute();
            log.info("Sprawdzono zaliczenie dla studenta o ID: {}", studentId);
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania zaliczenia dla studenta {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Nie udało się sprawdzić zaliczenia", e);
        }
    }

    /**
     * Pobiera średnią ważoną ocen dla podanego studenta.
     * W przypadku braku ocen zwraca Optional.empty().
     *
     * @param studentId identyfikator studenta (nie może być null)
     * @return Optional z wartością średniej (lub pusty, gdy brak ocen lub błąd)
     * @throws IllegalArgumentException jeśli studentId jest null lub <= 0
     */
    @Transactional(readOnly = true)
    public Optional<Double> pobierzSredniaStudenta(Long studentId) {
        if (studentId == null || studentId <= 0) {
            log.warn("Próba pobrania średniej z nieprawidłowym studentId: {}", studentId);
            throw new IllegalArgumentException("Identyfikator studenta musi być dodatni i niepusty");
        }

        try {
            String sql = "SELECT PKG_OCENY.SREDNIA_STUDENTA(?1) FROM DUAL";
            Object result = entityManager.createNativeQuery(sql)
                    .setParameter(1, studentId)
                    .getSingleResult();

            if (result == null) {
                log.debug("Brak ocen dla studenta o ID: {}", studentId);
                return Optional.empty();
            }

            if (result instanceof Number) {
                double srednia = ((Number) result).doubleValue();
                log.debug("Średnia dla studenta {}: {}", studentId, srednia);
                return Optional.of(srednia);
            } else {
                log.warn("Nieoczekiwany typ wyniku dla średniej studenta {}: {}", studentId, result.getClass());
                return Optional.empty();
            }
        } catch (jakarta.persistence.NoResultException e) {
            log.debug("Brak wyników (brak ocen) dla studenta: {}", studentId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania średniej dla studenta {}: {}", studentId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Generuje ranking studentów i wypisuje go do konsoli bazy danych (DBMS_OUTPUT).
     * Uruchamia procedurę składowaną PKG_OCENY.RANKING_STUDENTOW.
     *
     * @throws RuntimeException jeśli wystąpi błąd podczas wywołania procedury
     */
    @Transactional
    public void generujRankingWKonsoliBazy() {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PKG_OCENY.RANKING_STUDENTOW");
            query.execute();
            log.info("Ranking studentów został wygenerowany w konsoli bazy danych.");
        } catch (Exception e) {
            log.error("Błąd podczas generowania rankingu: {}", e.getMessage(), e);
            throw new RuntimeException("Nie udało się wygenerować rankingu", e);
        }
    }
}