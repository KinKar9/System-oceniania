package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "WARUNKI_ZAL")
public class WarunkiZal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_WARUNKU", updatable = false)
    private Long id;

    @NotNull(message = "Wymagany procent jest wymagany")
    @Min(value = 0, message = "Wymagany procent nie może być mniejszy niż 0")
    @Max(value = 100, message = "Wymagany procent nie może przekraczać 100")
    @Column(name = "WYMAGANA_PROCENT", nullable = false)
    private Integer wymaganaProcent;

    @DecimalMin(value = "0.0", message = "Średnia nie może być mniejsza niż 0")
    @DecimalMax(value = "10.0", message = "Średnia nie może przekraczać 10")
    @Digits(integer = 2, fraction = 2, message = "Średnia może mieć maksymalnie 2 miejsca po przecinku")
    @Column(name = "MINIMALNA_SREDNIA", precision = 4, scale = 2)
    private BigDecimal minimalnaSrednia = BigDecimal.valueOf(3.0);

    // Zmiana z String na boolean – bezpieczniej i czytelniej
    @NotNull(message = "Informacja o egzaminie jest wymagana")
    @Column(name = "CZY_WYMAGANY_EGZAMIN", nullable = false)
    private boolean czyWymaganyEgzamin = false;

    @NotNull(message = "Przedmiot jest wymagany")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRZEDMIOTU", nullable = false)
    private Przedmiot przedmiot;

    @Version
    @Column(name = "WERSJA")
    private Integer version;

    public WarunkiZal() {}

    // gettery i settery
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