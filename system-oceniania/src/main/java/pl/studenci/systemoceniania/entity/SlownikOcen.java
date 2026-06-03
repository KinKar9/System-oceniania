package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SLOWNIK_OCEN")
public class SlownikOcen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TYPU")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "NAZWA", nullable = false)
    private String nazwa;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    @Column(name = "WAGA", nullable = false)
    private Double waga;

    @Column(name = "DOMYSLNY_ZAKRES_MIN")
    private Double domyslnyZakresMin = 2.0;

    @Column(name = "DOMYSLNY_ZAKRES_MAX")
    private Double domyslnyZakresMax = 5.0;

    @OneToMany(mappedBy = "typ")
    private List<Ocena> oceny = new ArrayList<>();

    public SlownikOcen() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public Double getWaga() { return waga; }
    public void setWaga(Double waga) { this.waga = waga; }
    public Double getDomyslnyZakresMin() { return domyslnyZakresMin; }
    public void setDomyslnyZakresMin(Double domyslnyZakresMin) { this.domyslnyZakresMin = domyslnyZakresMin; }
    public Double getDomyslnyZakresMax() { return domyslnyZakresMax; }
    public void setDomyslnyZakresMax(Double domyslnyZakresMax) { this.domyslnyZakresMax = domyslnyZakresMax; }
    public List<Ocena> getOceny() { return oceny; }
    public void setOceny(List<Ocena> oceny) { this.oceny = oceny; }
}