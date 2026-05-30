package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.OcenaCzastkowa;

@Repository
public interface OcenaCzastkowaRepository
        extends JpaRepository<OcenaCzastkowa, Long> {
}