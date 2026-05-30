package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.Student;

@Repository
public interface StudentRepository
        extends JpaRepository<Student, Long> {
}