package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.Pracownik;

@Repository
public interface PracownikRepository extends JpaRepository<Pracownik, Long> {}