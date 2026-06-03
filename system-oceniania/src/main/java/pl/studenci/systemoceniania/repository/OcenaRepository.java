package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.studenci.systemoceniania.entity.Ocena;
import java.util.List;

public interface OcenaRepository extends JpaRepository<Ocena, Long> {
    List<Ocena> findByZapisStudentId(Long studentId);
    List<Ocena> findByZapisGrupaPrzedmiotId(Long przedmiotId);
    List<Ocena> findByZapisId(Long zapisId);

    // Filtrowanie i sortowanie dynamiczne za pomocą @Query
    @Query("SELECT o FROM Ocena o WHERE " +
            "(:studentId IS NULL OR o.zapis.student.id = :studentId) AND " +
            "(:przedmiotId IS NULL OR o.zapis.grupa.przedmiot.id = :przedmiotId) AND " +
            "(:typId IS NULL OR o.typ.id = :typId) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'wartosc' AND :order = 'asc' THEN o.wartosc END ASC, " +
            "CASE WHEN :sortBy = 'wartosc' AND :order = 'desc' THEN o.wartosc END DESC, " +
            "CASE WHEN :sortBy = 'data' AND :order = 'asc' THEN o.dataWystawienia END ASC, " +
            "CASE WHEN :sortBy = 'data' AND :order = 'desc' THEN o.dataWystawienia END DESC")
    List<Ocena> filterAndSort(@Param("studentId") Long studentId,
                              @Param("przedmiotId") Long przedmiotId,
                              @Param("typId") Long typId,
                              @Param("sortBy") String sortBy,
                              @Param("order") String order);

    // Sprawdzenie czy istnieje już ocena danego typu dla zapisu
    boolean existsByZapisIdAndTypId(Long zapisId, Long typId);
}