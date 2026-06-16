package pl.studenci.systemoceniania.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Ranking;
import pl.studenci.systemoceniania.repository.RankingRepository;

import java.util.Optional;

@Service
@Transactional
public class RankingService {

    private static final Logger log = LoggerFactory.getLogger(RankingService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    /**
     * Generuje ranking studentów poprzez procedurę składowaną w PostgreSQL.
     * @param semesterId identyfikator semestru (opcjonalny) – walidowany przed przekazaniem
     */
    public void generateRanking(String semesterId) {
        if (semesterId != null && !isValidSemesterId(semesterId)) {
            log.warn("Nieprawidłowy format identyfikatora semestru: {}", semesterId);
            throw new IllegalArgumentException("Nieprawidłowy format identyfikatora semestru. Dozwolone: cyfry, myślniki, litery i spacje.");
        }

        try {
            // PostgreSQL – procedura bez pakietu, z nazwanym parametrem
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("ranking_studentow");

            // Jawne rejestrowanie parametru nazwanego
            query.registerStoredProcedureParameter("p_semestr", String.class, ParameterMode.IN);

            if (semesterId != null && !semesterId.isEmpty()) {
                query.setParameter("p_semestr", semesterId);
            } else {
                query.setParameter("p_semestr", null);
            }

            query.execute();
            log.info("Ranking został pomyślnie wygenerowany dla semestru: {}", semesterId);
        } catch (Exception e) {
            log.error("Błąd podczas generowania rankingu: {}", e.getMessage(), e);
            throw new RuntimeException("Nie udało się wygenerować rankingu", e);
        }
    }

    /**
     * Pobiera ostatni wygenerowany ranking (na podstawie ID – zakładając, że wyższe ID = nowszy).
     * @return Optional z rankingiem lub pusty Optional
     */
    public Optional<Ranking> getLatestRanking() {
        try {
            return rankingRepository.findTopByOrderByIdDesc();
        } catch (Exception e) {
            log.error("Błąd podczas pobierania ostatniego rankingu: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Waliduje format identyfikatora semestru.
     */
    private boolean isValidSemesterId(String semesterId) {
        return semesterId != null && semesterId.matches("^[a-zA-Z0-9\\-\\_\\s]+$");
    }
}