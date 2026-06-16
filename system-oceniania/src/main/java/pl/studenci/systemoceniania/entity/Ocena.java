package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "oceny")
public class Ocena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oceny", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_zapisu", nullable = false)
    private Zapisy zapis;

    @ManyToOne
    @JoinColumn(name = "id_typu", nullable = false)
    private SlownikOcen typ;

    @NotNull(message = "Wartość oceny jest wymagana")
    @DecimalMin(value = "2.0", message = "Ocena nie może być mniejsza niż 2.0")
    @DecimalMax(value = "5.0", message = "Ocena nie może być większa niż 5.0")
    @Column(name = "wartosc", nullable = false)
    private Double wartosc;

    @Column(name = "data_wystawienia", updatable = false)
    private LocalDate dataWystawienia;

    @Size(max = 200, message = "Komentarz może mieć maksymalnie 200 znaków")
    @Column(name = "komentarz")
    private String komentarz;

    public Ocena() {
        this.dataWystawienia = LocalDate.now();
    }

    @PrePersist
    protected void onCreate() {
        if (dataWystawienia == null) {
            dataWystawienia = LocalDate.now();
        }
    }

    // gettery i settery
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ocena)) return false;
        Ocena ocena = (Ocena) o;
        return id != null && Objects.equals(id, ocena.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Ocena{" +
                "id=" + id +
                ", wartosc=" + wartosc +
                ", dataWystawienia=" + dataWystawienia +
                ", komentarz='" + komentarz + '\'' +
                '}';
    }
}