package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.PlanZajec;

import java.util.List;

@Repository
public interface PlanZajecRepository extends JpaRepository<PlanZajec, Integer> {  // ← Integer
    List<PlanZajec> findByPracownikId(Long pracownikId);
    List<PlanZajec> findByAktywnyTrue();
}