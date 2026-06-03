package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import java.util.Optional;

public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Long> {
    Optional<Uzytkownik> findByUsername(String username);
    Optional<Uzytkownik> findByEmail(String email);
}