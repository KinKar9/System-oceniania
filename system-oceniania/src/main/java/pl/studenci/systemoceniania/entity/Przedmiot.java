package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "przedmioty")
public class Przedmiot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_przedmiotu", updatable = false)
    private Long id;

    @NotBlank(message = "Kod przedmiotu jest wymagany")
    @Size(max = 15, message = "Kod może mieć maksymalnie 15 znaków")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Kod może zawierać tylko duże litery, cyfry i myślniki")
    @Column(name = "kod_przedmiotu", nullable = false, unique = true, updatable = false, length = 15)
    private String kodPrzedmiotu;

    @NotBlank(message = "Nazwa przedmiotu jest wymagana")
    @Size(max = 150, message = "Nazwa może mieć maksymalnie 150 znaków")
    @Column(name = "nazwa", nullable = false, length = 150)
    private String nazwa;

    @NotNull(message = "ECTS jest wymagane")
    @Min(value = 1, message = "ECTS nie może być mniejsze niż 1")
    @Max(value = 30, message = "ECTS nie może przekraczać 30")
    @Column(name = "ects", nullable = false)
    private Integer ects;

    @NotNull(message = "Kierunek jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kierunek_id", nullable = false)
    private Kierunek kierunek;

    @OneToMany(mappedBy = "przedmiot", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Grupa> grupy = new ArrayList<>();

    @OneToOne(mappedBy = "przedmiot", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private WarunkiZal warunkiZal;

    @Version
    @Column(name = "wersja")
    private Integer version;

    public Przedmiot() {}

    public void addGrupa(Grupa grupa) {
        grupy.add(grupa);
        grupa.setPrzedmiot(this);
    }

    public void removeGrupa(Grupa grupa) {
        grupy.remove(grupa);
        grupa.setPrzedmiot(null);
    }

    public void setWarunkiZal(WarunkiZal warunkiZal) {
        if (this.warunkiZal != null) {
            this.warunkiZal.setPrzedmiot(null);
        }
        this.warunkiZal = warunkiZal;
        if (warunkiZal != null) {
            warunkiZal.setPrzedmiot(this);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKodPrzedmiotu() { return kodPrzedmiotu; }
    public void setKodPrzedmiotu(String kodPrzedmiotu) { this.kodPrzedmiotu = kodPrzedmiotu; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public Integer getEcts() { return ects; }
    public void setEcts(Integer ects) { this.ects = ects; }
    public Kierunek getKierunek() { return kierunek; }
    public void setKierunek(Kierunek kierunek) { this.kierunek = kierunek; }
    public List<Grupa> getGrupy() { return grupy; }
    public void setGrupy(List<Grupa> grupy) { this.grupy = grupy; }
    public WarunkiZal getWarunkiZal() { return warunkiZal; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Przedmiot)) return false;
        Przedmiot that = (Przedmiot) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Przedmiot{" +
                "id=" + id +
                ", kodPrzedmiotu='" + kodPrzedmiotu + '\'' +
                ", nazwa='" + nazwa + '\'' +
                ", ects=" + ects +
                '}';
    }
}