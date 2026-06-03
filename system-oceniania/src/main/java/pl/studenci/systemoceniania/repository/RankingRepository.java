package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
}