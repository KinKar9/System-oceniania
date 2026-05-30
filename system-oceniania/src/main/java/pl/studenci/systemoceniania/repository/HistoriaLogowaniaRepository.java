package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.HistoriaLogowania;

public interface HistoriaLogowaniaRepository extends JpaRepository<HistoriaLogowania, Long> {
}