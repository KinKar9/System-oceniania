package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRZEDMIOTY")
public class Przedmiot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRZEDMIOTU")
    private Long id;

    @NotBlank
    @Size(max = 15)
    @Column(name = "KOD_PRZEDMIOTU", nullable = false, unique = true)
    private String kodPrzedmiotu;

    @NotBlank
    @Size(max = 150)
    @Column(name = "NAZWA", nullable = false)
    private String nazwa;

    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "ECTS", nullable = false)
    private Integer ects;

    @ManyToOne
    @JoinColumn(name = "KIERUNEK_ID", nullable = false)
    private Kierunek kierunek;

    @OneToMany(mappedBy = "przedmiot", cascade = CascadeType.ALL)
    private List<Grupa> grupy = new ArrayList<>();

    @OneToOne(mappedBy = "przedmiot", cascade = CascadeType.ALL)
    private WarunkiZal warunkiZal;

    // konstruktory, gettery, settery (pomiń dla brevity, ale dodaj wszystkie)
    public Przedmiot() {}
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
    public void setWarunkiZal(WarunkiZal warunkiZal) { this.warunkiZal = warunkiZal; }
}