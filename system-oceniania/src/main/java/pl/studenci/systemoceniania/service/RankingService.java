package pl.studenci.systemoceniania.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Ranking;
import pl.studenci.systemoceniania.repository.RankingRepository;

@Service
public class RankingService {
    @PersistenceContext private EntityManager em;
    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) { this.rankingRepository = rankingRepository; }

    public void generujRankingDoBazy(String idSemestru) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("PKG_OCENY.RANKING_STUDENTOW");
        if (idSemestru != null && !idSemestru.isEmpty()) {
            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            query.setParameter(1, idSemestru);
        }
        query.execute();
    }

    public Ranking getLastRanking() {
        return rankingRepository.findAll().stream().reduce((a,b) -> a.getId() > b.getId() ? a : b).orElse(null);
    }
}