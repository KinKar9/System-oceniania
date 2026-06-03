package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.studenci.systemoceniania.entity.Zapisy;
import java.util.List;
import java.util.Optional;

public interface ZapisyRepository extends JpaRepository<Zapisy, Long> {
    List<Zapisy> findByStudentId(Long studentId);
    List<Zapisy> findByGrupaId(Long grupaId);
    Optional<Zapisy> findByStudentIdAndGrupaId(Long studentId, Long grupaId);
    List<Zapisy> findByStudentIdAndStatus(Long studentId, String status);
}