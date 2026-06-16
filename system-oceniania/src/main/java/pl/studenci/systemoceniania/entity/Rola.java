package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "rola", indexes = {
        @Index(name = "idx_rola_nazwa", columnList = "nazwa_roli", unique = true)
})
public class Rola implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_roli", updatable = false)
    private Long id;

    @NotNull(message = "Nazwa roli jest wymagana")
    @Enumerated(EnumType.STRING)
    @Column(name = "nazwa_roli", nullable = false, unique = true, length = 30)
    private NazwaRoli nazwaRoli;

    @CreationTimestamp
    @Column(name = "data_utworzenia", updatable = false)
    private LocalDateTime dataUtworzenia;

    @UpdateTimestamp
    @Column(name = "data_aktualizacji")
    private LocalDateTime dataAktualizacji;

    @ManyToMany(mappedBy = "role")
    private Set<Uzytkownik> uzytkownicy = new HashSet<>();

    public enum NazwaRoli {
        ADMIN, PRACOWNIK, STUDENT
    }

    public Rola() {}

    public Rola(NazwaRoli nazwaRoli) {
        this.nazwaRoli = nazwaRoli;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NazwaRoli getNazwaRoli() { return nazwaRoli; }
    public void setNazwaRoli(NazwaRoli nazwaRoli) { this.nazwaRoli = nazwaRoli; }
    public LocalDateTime getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(LocalDateTime dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }
    public LocalDateTime getDataAktualizacji() { return dataAktualizacji; }
    public void setDataAktualizacji(LocalDateTime dataAktualizacji) { this.dataAktualizacji = dataAktualizacji; }
    public Set<Uzytkownik> getUzytkownicy() { return uzytkownicy; }
    public void setUzytkownicy(Set<Uzytkownik> uzytkownicy) { this.uzytkownicy = uzytkownicy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rola)) return false;
        Rola rola = (Rola) o;
        return id != null && Objects.equals(id, rola.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Rola{" +
                "id=" + id +
                ", nazwaRoli=" + nazwaRoli +
                '}';
    }
}