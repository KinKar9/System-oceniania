package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.WarunkiZal;
import java.util.Optional;

public interface WarunkiZalRepository extends JpaRepository<WarunkiZal, Long> {
    Optional<WarunkiZal> findByPrzedmiotId(Long przedmiotId);
}