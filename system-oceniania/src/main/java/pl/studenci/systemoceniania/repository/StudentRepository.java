package pl.studenci.systemoceniania.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.studenci.systemoceniania.entity.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 🔥 PROSTA METODA – znajdź studenta po tokenie (bez JOIN FETCH)
    Optional<Student> findBySecureToken(String token);

    Optional<Student> findByEmail(String email);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.zapisy WHERE s.email = :email")
    Optional<Student> findByEmailWithZapisy(@Param("email") String email);

    @Query("""
        SELECT DISTINCT s FROM Student s
        LEFT JOIN FETCH s.zapisy z
        LEFT JOIN FETCH z.oceny
        WHERE s.secureToken = :token
        """)
    Optional<Student> findBySecureTokenWithOceny(@Param("token") String token);

    Optional<Student> findByNrIndeksu(String nrIndeksu);

    // ============================================================
    // 🔥 BEZPOŚREDNIE AKTUALIZACJE
    // ============================================================

    @Modifying
    @Query("UPDATE Student s SET s.secureToken = NULL WHERE s.id = :studentId")
    int clearSecureToken(@Param("studentId") Long studentId);

    @Modifying
    @Query("UPDATE Student s SET s.secureToken = :token WHERE s.id = :studentId")
    int updateSecureToken(@Param("studentId") Long studentId, @Param("token") String token);
}