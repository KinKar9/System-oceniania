package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.studenci.systemoceniania.entity.Kierunek;
import java.util.List;
import java.util.Optional;

public interface KierunekRepository extends JpaRepository<Kierunek, Long> {

    @Query("SELECT k FROM Kierunek k WHERE k.deleted = false")
    List<Kierunek> findAll();

    @Query("SELECT k FROM Kierunek k WHERE k.id = :id AND k.deleted = false")
    Optional<Kierunek> findById(@Param("id") Long id);

    @Query("SELECT k FROM Kierunek k")
    List<Kierunek> findAllIncludingDeleted();

    @Query("SELECT k FROM Kierunek k WHERE k.id = :id AND k.deleted = true")
    Optional<Kierunek> findDeletedById(@Param("id") Long id);

    Optional<Kierunek> findByKodKierunku(String kodKierunku);
}