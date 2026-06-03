package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.LogiSystemu;

public interface LogiSystemuRepository extends JpaRepository<LogiSystemu, Long> {
}