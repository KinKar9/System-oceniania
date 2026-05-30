package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.OcenaCzastkowa;

import java.util.List;

@Repository
public interface OcenaCzastkowaRepository
        extends JpaRepository<OcenaCzastkowa, Long> {
    List<OcenaCzastkowa> findByStudentId(Long studentId);
}