package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "OCENY")
public class Ocena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_OCENY")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ZAPISU", nullable = false)
    private Zapisy zapis;

    @ManyToOne
    @JoinColumn(name = "ID_TYPU", nullable = false)
    private SlownikOcen typ;

    @NotNull
    @DecimalMin("2.0")
    @DecimalMax("5.0")
    @Column(name = "WARTOSC", nullable = false)
    private Double wartosc;

    @Column(name = "DATA_WYSTAWIENIA")
    private LocalDate dataWystawienia = LocalDate.now();

    @Size(max = 200)
    @Column(name = "KOMENTARZ")
    private String komentarz;

    public Ocena() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Zapisy getZapis() { return zapis; }
    public void setZapis(Zapisy zapis) { this.zapis = zapis; }
    public SlownikOcen getTyp() { return typ; }
    public void setTyp(SlownikOcen typ) { this.typ = typ; }
    public Double getWartosc() { return wartosc; }
    public void setWartosc(Double wartosc) { this.wartosc = wartosc; }
    public LocalDate getDataWystawienia() { return dataWystawienia; }
    public void setDataWystawienia(LocalDate dataWystawienia) { this.dataWystawienia = dataWystawienia; }
    public String getKomentarz() { return komentarz; }
    public void setKomentarz(String komentarz) { this.komentarz = komentarz; }
}