package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "SEMESTRY", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nazwa")
})
public class Semestr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SEMESTRU", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa semestru jest wymagana")
    @Size(max = 20, message = "Nazwa semestru może mieć maksymalnie 20 znaków")
    @Column(name = "NAZWA", nullable = false, unique = true, length = 20)
    private String nazwa;

    public Semestr() {}
    public Semestr(String nazwa) { this.nazwa = nazwa; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Semestr)) return false;
        Semestr semestr = (Semestr) o;
        return id != null && Objects.equals(id, semestr.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Semestr{" + "id=" + id + ", nazwa='" + nazwa + '\'' + '}';
    }
}