package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.SlownikOcen;

public interface SlownikOcenRepository extends JpaRepository<SlownikOcen, Long> {
}