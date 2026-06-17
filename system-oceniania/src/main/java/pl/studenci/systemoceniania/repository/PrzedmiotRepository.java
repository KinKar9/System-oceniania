package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.Przedmiot;

@Repository
public interface PrzedmiotRepository extends JpaRepository<Przedmiot, Integer> {
}