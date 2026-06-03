package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Grupa;
import java.util.List;

public interface GrupaRepository extends JpaRepository<Grupa, Long> {
    List<Grupa> findByPrzedmiotId(Long przedmiotId);
    List<Grupa> findByPracownikId(Long pracownikId);
}