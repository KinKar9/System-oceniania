package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.PozycjaPlanu;
import pl.studenci.systemoceniania.enums.DzienTygodnia;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PozycjaPlanuRepository extends JpaRepository<PozycjaPlanu, Long> {

    @Query("SELECT COUNT(p) > 0 FROM PozycjaPlanu p " +
            "WHERE p.sala.id = :salaId " +
            "AND p.dzienTygodnia = :dzien " +
            "AND p.id != :excludeId " +
            "AND ((p.godzinaRozpoczecia < :koniec AND p.godzinaZakonczenia > :poczatek))")
    boolean existsConflictForSala(@Param("salaId") Long salaId,
                                  @Param("dzien") DzienTygodnia dzien,
                                  @Param("poczatek") LocalTime poczatek,
                                  @Param("koniec") LocalTime koniec,
                                  @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(p) > 0 FROM PozycjaPlanu p " +
            "WHERE p.prowadzacy.id = :prowadzacyId " +
            "AND p.dzienTygodnia = :dzien " +
            "AND p.id != :excludeId " +
            "AND ((p.godzinaRozpoczecia < :koniec AND p.godzinaZakonczenia > :poczatek))")
    boolean existsConflictForProwadzacy(@Param("prowadzacyId") Long prowadzacyId,
                                        @Param("dzien") DzienTygodnia dzien,
                                        @Param("poczatek") LocalTime poczatek,
                                        @Param("koniec") LocalTime koniec,
                                        @Param("excludeId") Long excludeId);

    @Query("SELECT p FROM PozycjaPlanu p " +
            "JOIN p.grupa g " +
            "JOIN g.zapisy z " +
            "WHERE z.student.id = :studentId")
    List<PozycjaPlanu> findPozycjeForStudent(@Param("studentId") Long studentId);
}