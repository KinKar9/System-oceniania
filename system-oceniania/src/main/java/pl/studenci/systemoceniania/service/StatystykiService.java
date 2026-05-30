package pl.studenci.systemoceniania.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StatystykiService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void sprawdzZaliczenie(Long studentId) {

        StoredProcedureQuery query =
                entityManager.createStoredProcedureQuery(
                        "PKG_OCENY.SPRAWDZ_ZALICZENIE");

        query.registerStoredProcedureParameter(
                1,
                Long.class,
                ParameterMode.IN);

        query.setParameter(1, studentId);

        query.execute();
    }

    public Double pobierzSredniaStudenta(Long studentId) {
        String sql = "SELECT PKG_OCENY.SREDNIA_STUDENTA(?1) FROM DUAL";

        Number wynik = (Number) entityManager.createNativeQuery(sql)
                .setParameter(1, studentId)
                .getSingleResult();

        return wynik != null ? wynik.doubleValue() : 0.0;
    }

    public void generujRankingWKonsoliBazy() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                "PKG_OCENY.RANKING_STUDENTOW"
        );
        query.execute();
    }


}