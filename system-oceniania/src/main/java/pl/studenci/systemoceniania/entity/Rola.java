package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ROLE", indexes = {
        @Index(name = "idx_rola_nazwa", columnList = "NAZWA_ROLI", unique = true)
})
public class Rola implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROLI", updatable = false)
    private Long id;

    @NotNull(message = "Nazwa roli jest wymagana")
    @Enumerated(EnumType.STRING)
    @Column(name = "NAZWA_ROLI", nullable = false, unique = true, length = 30)
    private NazwaRoli nazwaRoli;

    // Pola audytowe
    @CreationTimestamp
    @Column(name = "DATA_UTWORZENIA", updatable = false)
    private LocalDateTime dataUtworzenia;

    @UpdateTimestamp
    @Column(name = "DATA_AKTUALIZACJI")
    private LocalDateTime dataAktualizacji;

    // Relacja zwrotna do Uzytkownik
    @ManyToMany(mappedBy = "role")
    private Set<Uzytkownik> uzytkownicy = new HashSet<>();

    // Enum z dozwolonymi rolami
    public static enum NazwaRoli
    {
        ADMIN, PRACOWNIK, STUDENT
    }
    public Rola() {}

    public Rola(NazwaRoli nazwaRoli) {
        this.nazwaRoli = nazwaRoli;
    }

    // gettery i settery
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