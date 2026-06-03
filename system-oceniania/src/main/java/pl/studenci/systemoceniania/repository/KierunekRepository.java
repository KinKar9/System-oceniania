package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Kierunek;
import java.util.Optional;

public interface KierunekRepository extends JpaRepository<Kierunek, Long> {
    Optional<Kierunek> findByKodKierunku(String kodKierunku);
}