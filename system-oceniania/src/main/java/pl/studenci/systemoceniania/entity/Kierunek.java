package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KIERUNKI")
public class Kierunek {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_KIERUNKU")
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "NAZWA", nullable = false, unique = true)
    private String nazwa;

    @NotBlank
    @Size(max = 10)
    @Column(name = "KOD_KIERUNKU", nullable = false, unique = true)
    private String kodKierunku;

    @NotNull
    @Min(1)
    @Max(2)
    @Column(name = "STOPIEN", nullable = false)
    private Integer stopien;  // 1 = licencjat/inż, 2 = magister

    @OneToMany(mappedBy = "kierunek", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Przedmiot> przedmioty = new ArrayList<>();

    public Kierunek() {}

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public String getKodKierunku() { return kodKierunku; }
    public void setKodKierunku(String kodKierunku) { this.kodKierunku = kodKierunku; }
    public Integer getStopien() { return stopien; }
    public void setStopien(Integer stopien) { this.stopien = stopien; }
    public List<Przedmiot> getPrzedmioty() { return przedmioty; }
    public void setPrzedmioty(List<Przedmiot> przedmioty) { this.przedmioty = przedmioty; }
}