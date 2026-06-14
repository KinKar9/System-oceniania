package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.studenci.systemoceniania.entity.Ocena;

import java.time.LocalDate;
import java.util.List;

public interface OcenaRepository extends JpaRepository<Ocena, Long> {
    List<Ocena> findByZapisStudentId(Long studentId);
    List<Ocena> findByZapisGrupaPrzedmiotId(Long przedmiotId);
    List<Ocena> findByZapisId(Long zapisId);

    @Query("SELECT o FROM Ocena o JOIN FETCH o.zapis z JOIN FETCH z.student JOIN FETCH z.grupa g JOIN FETCH g.przedmiot JOIN FETCH o.typ WHERE z.student.id = :studentId")
    List<Ocena> findOcenyStudentaWithDetails(@Param("studentId") Long studentId);

    // Filtrowanie i sortowanie dynamiczne
    @Query("SELECT o FROM Ocena o " +
            "JOIN FETCH o.zapis z " +
            "JOIN FETCH z.student s " +
            "JOIN FETCH z.grupa g " +
            "JOIN FETCH g.przedmiot " +
            "JOIN FETCH o.typ " +
            "WHERE (:studentId IS NULL OR s.id = :studentId) " +
            "AND (:przedmiotId IS NULL OR g.przedmiot.id = :przedmiotId) " +
            "AND (:typId IS NULL OR o.typ.id = :typId) " +
            "AND (:dataOd IS NULL OR o.dataWystawienia >= :dataOd) " +   // filtr daty
            "ORDER BY " +
            "CASE WHEN :sortBy = 'wartosc' AND :order = 'asc' THEN o.wartosc END ASC, " +
            "CASE WHEN :sortBy = 'wartosc' AND :order = 'desc' THEN o.wartosc END DESC, " +
            "CASE WHEN :sortBy = 'data' AND :order = 'asc' THEN o.dataWystawienia END ASC, " +
            "CASE WHEN :sortBy = 'data' AND :order = 'desc' THEN o.dataWystawienia END DESC, " +
            "CASE WHEN :sortBy = 'nazwisko' AND :order = 'asc' THEN s.nazwisko END ASC, " +
            "CASE WHEN :sortBy = 'nazwisko' AND :order = 'desc' THEN s.nazwisko END DESC")
    List<Ocena> filterAndSort(@Param("studentId") Long studentId,
                              @Param("przedmiotId") Long przedmiotId,
                              @Param("typId") Long typId,
                              @Param("dataOd") LocalDate dataOd,
                              @Param("sortBy") String sortBy,
                              @Param("order") String order);

    boolean existsByZapisIdAndTypId(Long zapisId, Long typId);

    @Query("SELECT o FROM Ocena o JOIN FETCH o.zapis z JOIN FETCH z.grupa g JOIN FETCH g.przedmiot p " +
            "ORDER BY (SELECT COUNT(o2) FROM Ocena o2 WHERE o2.zapis.grupa.przedmiot = p) DESC")
    List<Ocena> findAllSortedByPopularnoscPrzedmiotu();
}