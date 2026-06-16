package pl.studenci.systemoceniania.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.studenci.systemoceniania.entity.Przedmiot;

@Entity
@Table(name = "kierunki")
public class Kierunek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kierunku", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa kierunku jest wymagana")
    @Size(max = 150, message = "Nazwa może mieć maksymalnie 150 znaków")
    @Column(name = "nazwa", nullable = false, unique = true, length = 150)
    private String nazwa;

    @NotBlank(message = "Kod kierunku jest wymagany")
    @Size(max = 10, message = "Kod może mieć maksymalnie 10 znaków")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Kod może zawierać tylko duże litery i cyfry")
    @Column(name = "kod_kierunku", nullable = false, unique = true, length = 10)
    private String kodKierunku;

    @NotNull(message = "Stopień jest wymagany")
    @Enumerated(EnumType.STRING)
    @Column(name = "stopien", nullable = false, length = 20)
    private Stopien stopien;

    // SOFT DELETE – flaga usunięcia logicznego
    @Column(name = "deleted")
    private boolean deleted = false;

    // BEZ CascadeType.ALL – nie chcemy fizycznego usuwania przedmiotów
    @OneToMany(mappedBy = "kierunek", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonManagedReference
    private List<Przedmiot> przedmioty = new ArrayList<>();

    public enum Stopien {
        INZYNIER_LICENCJAT(1, "I stopień (inżynier/licencjat)"),
        MAGISTER(2, "II stopień (magister)");

        private final int wartosc;
        private final String opis;

        Stopien(int wartosc, String opis) {
            this.wartosc = wartosc;
            this.opis = opis;
        }

        public int getWartosc() { return wartosc; }
        public String getOpis() { return opis; }

        public static Stopien fromWartosc(int wartosc) {
            for (Stopien s : values()) {
                if (s.wartosc == wartosc) return s;
            }
            throw new IllegalArgumentException("Nieprawidłowa wartość stopnia: " + wartosc);
        }
    }

    public Kierunek() {}

    public void addPrzedmiot(Przedmiot przedmiot) {
        przedmioty.add(przedmiot);
        przedmiot.setKierunek(this);
    }

    public void removePrzedmiot(Przedmiot przedmiot) {
        przedmioty.remove(przedmiot);
        przedmiot.setKierunek(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public String getKodKierunku() { return kodKierunku; }
    public void setKodKierunku(String kodKierunku) { this.kodKierunku = kodKierunku; }
    public Stopien getStopien() { return stopien; }
    public void setStopien(Stopien stopien) { this.stopien = stopien; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public List<Przedmiot> getPrzedmioty() { return przedmioty; }
    public void setPrzedmioty(List<Przedmiot> przedmioty) { this.przedmioty = przedmioty; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kierunek)) return false;
        Kierunek kierunek = (Kierunek) o;
        return id != null && Objects.equals(id, kierunek.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Kierunek{" +
                "id=" + id +
                ", nazwa='" + nazwa + '\'' +
                ", kodKierunku='" + kodKierunku + '\'' +
                ", stopien=" + stopien +
                ", deleted=" + deleted +
                '}';
    }
}