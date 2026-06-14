package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "SLOWNIK_OCEN", uniqueConstraints = {
        @UniqueConstraint(columnNames = "NAZWA")
})
public class SlownikOcen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TYPU", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa typu oceny jest wymagana")
    @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
    @Column(name = "NAZWA", nullable = false, unique = true, length = 50)
    private String nazwa;

    @NotNull(message = "Waga jest wymagana")
    @DecimalMin(value = "0.0", inclusive = true, message = "Waga nie może być mniejsza niż 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "Waga nie może być większa niż 1")
    @Column(name = "WAGA", nullable = false, precision = 5, scale = 4)
    private BigDecimal waga;

    @DecimalMin(value = "0.0", message = "Zakres minimalny nie może być mniejszy niż 0")
    @Column(name = "DOMYSLNY_ZAKRES_MIN", precision = 3, scale = 1)
    private BigDecimal domyslnyZakresMin = BigDecimal.valueOf(2.0);

    @DecimalMax(value = "10.0", message = "Zakres maksymalny nie może przekraczać 10")
    @Column(name = "DOMYSLNY_ZAKRES_MAX", precision = 3, scale = 1)
    private BigDecimal domyslnyZakresMax = BigDecimal.valueOf(5.0);

    @OneToMany(mappedBy = "typ", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataWystawienia DESC")
    private List<Ocena> oceny = new ArrayList<>();

    // Walidacja spójności zakresów (min < max)
    @AssertTrue(message = "Zakres minimalny musi być mniejszy niż maksymalny")
    private boolean isValidRange() {
        if (domyslnyZakresMin == null || domyslnyZakresMax == null) {
            return true; // jeśli null, pomijamy (użyjemy domyślnych)
        }
        return domyslnyZakresMin.compareTo(domyslnyZakresMax) < 0;
    }

    public SlownikOcen() {}

    // pomocnicze metody do zarządzania kolekcją
    public void addOcena(Ocena ocena) {
        oceny.add(ocena);
        ocena.setTyp(this);
    }

    public void removeOcena(Ocena ocena) {
        oceny.remove(ocena);
        ocena.setTyp(null);
    }

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public BigDecimal getWaga() { return waga; }
    public void setWaga(BigDecimal waga) { this.waga = waga; }
    public BigDecimal getDomyslnyZakresMin() { return domyslnyZakresMin; }
    public void setDomyslnyZakresMin(BigDecimal domyslnyZakresMin) { this.domyslnyZakresMin = domyslnyZakresMin; }
    public BigDecimal getDomyslnyZakresMax() { return domyslnyZakresMax; }
    public void setDomyslnyZakresMax(BigDecimal domyslnyZakresMax) { this.domyslnyZakresMax = domyslnyZakresMax; }
    public List<Ocena> getOceny() { return oceny; }
    public void setOceny(List<Ocena> oceny) { this.oceny = oceny; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlownikOcen)) return false;
        SlownikOcen that = (SlownikOcen) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SlownikOcen{" +
                "id=" + id +
                ", nazwa='" + nazwa + '\'' +
                ", waga=" + waga +
                ", zakresMin=" + domyslnyZakresMin +
                ", zakresMax=" + domyslnyZakresMax +
                '}';
    }
}