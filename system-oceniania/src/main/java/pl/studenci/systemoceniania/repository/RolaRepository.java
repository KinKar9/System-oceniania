package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Rola;
import java.util.Optional;

public interface RolaRepository extends JpaRepository<Rola, Long> {
    Optional<Rola> findByNazwaRoli(Rola.NazwaRoli nazwaRoli);
}