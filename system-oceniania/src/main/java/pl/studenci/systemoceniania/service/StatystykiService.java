@Service
public class StatystykiService {

    @PersistenceContext
    private EntityManager entityManager;

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
}