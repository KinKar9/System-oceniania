package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Semestr;

public interface SemestrRepository extends JpaRepository<Semestr, Long> {
}