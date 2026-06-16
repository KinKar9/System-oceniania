package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Objects;

// BRAKUJĄCY IMPORT:
import pl.studenci.systemoceniania.entity.Przedmiot;

@Entity
@Table(name = "warunki_zal")
public class WarunkiZal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_warunku", updatable = false)
    private Long id;

    @NotNull(message = "Wymagany procent jest wymagany")
    @Min(value = 0, message = "Wymagany procent nie może być mniejszy niż 0")
    @Max(value = 100, message = "Wymagany procent nie może przekraczać 100")
    @Column(name = "wymagana_procent", nullable = false)
    private Integer wymaganaProcent;

    @DecimalMin(value = "0.0", message = "Średnia nie może być mniejsza niż 0")
    @DecimalMax(value = "10.0", message = "Średnia nie może przekraczać 10")
    @Digits(integer = 2, fraction = 2, message = "Średnia może mieć maksymalnie 2 miejsca po przecinku")
    @Column(name = "minimalna_srednia", precision = 4, scale = 2)
    private BigDecimal minimalnaSrednia = BigDecimal.valueOf(3.0);

    @NotNull(message = "Informacja o egzaminie jest wymagana")
    @Column(name = "czy_wymagany_egzamin", nullable = false)
    private boolean czyWymaganyEgzamin = false;

    @NotNull(message = "Przedmiot jest wymagany")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_przedmiotu", nullable = false)
    private Przedmiot przedmiot;

    @Version
    @Column(name = "wersja")
    private Integer version;

    public WarunkiZal() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getWymaganaProcent() { return wymaganaProcent; }
    public void setWymaganaProcent(Integer wymaganaProcent) { this.wymaganaProcent = wymaganaProcent; }
    public BigDecimal getMinimalnaSrednia() { return minimalnaSrednia; }
    public void setMinimalnaSrednia(BigDecimal minimalnaSrednia) { this.minimalnaSrednia = minimalnaSrednia; }
    public boolean isCzyWymaganyEgzamin() { return czyWymaganyEgzamin; }
    public void setCzyWymaganyEgzamin(boolean czyWymaganyEgzamin) { this.czyWymaganyEgzamin = czyWymaganyEgzamin; }
    public Przedmiot getPrzedmiot() { return przedmiot; }
    public void setPrzedmiot(Przedmiot przedmiot) { this.przedmiot = przedmiot; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarunkiZal)) return false;
        WarunkiZal that = (WarunkiZal) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "WarunkiZal{" +
                "id=" + id +
                ", wymaganaProcent=" + wymaganaProcent +
                ", minimalnaSrednia=" + minimalnaSrednia +
                ", czyWymaganyEgzamin=" + czyWymaganyEgzamin +
                '}';
    }
}