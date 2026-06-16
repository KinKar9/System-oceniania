package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.studenci.systemoceniania.entity.Ocena;

import java.time.LocalDate;
import java.util.List;

public interface OcenaRepository extends JpaRepository<Ocena, Long>, JpaSpecificationExecutor<Ocena> {

    List<Ocena> findByZapisStudentId(Long studentId);
    List<Ocena> findByZapisGrupaPrzedmiotId(Long przedmiotId);
    List<Ocena> findByZapisId(Long zapisId);

    @Query("SELECT o FROM Ocena o JOIN FETCH o.zapis z JOIN FETCH z.student JOIN FETCH z.grupa g JOIN FETCH g.przedmiot JOIN FETCH o.typ WHERE z.student.id = :studentId")
    List<Ocena> findOcenyStudentaWithDetails(@Param("studentId") Long studentId);

    boolean existsByZapisIdAndTypId(Long zapisId, Long typId);

    @Query("SELECT o FROM Ocena o JOIN FETCH o.zapis z JOIN FETCH z.grupa g JOIN FETCH g.przedmiot p " +
            "ORDER BY (SELECT COUNT(o2) FROM Ocena o2 WHERE o2.zapis.grupa.przedmiot.id = p.id) DESC")
    List<Ocena> findAllSortedByPopularnoscPrzedmiotu();
}