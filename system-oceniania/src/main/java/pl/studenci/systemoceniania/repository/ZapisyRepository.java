package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Zapisy;
import java.util.List;
import java.util.Optional;

public interface ZapisyRepository extends JpaRepository<Zapisy, Long> {

    List<Zapisy> findByStudent_Id(Long studentId);
    List<Zapisy> findByGrupa_Id(Long grupaId);
    Optional<Zapisy> findByStudent_IdAndGrupa_Id(Long studentId, Long grupaId);
    List<Zapisy> findByStudent_IdAndStatus(Long studentId, String status);
    long countByGrupa_Id(Long grupaId);
    List<Zapisy> findByStatus(Zapisy.StatusZapisu status);
}