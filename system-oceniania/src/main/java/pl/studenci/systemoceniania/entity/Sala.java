package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "sale")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sala_seq")
    @SequenceGenerator(name = "sala_seq", sequenceName = "sala_seq", allocationSize = 1)
    @Column(name = "id_sali", updatable = false)
    private Long id;

    @NotBlank(message = "Numer sali jest wymagany")
    @Size(max = 20, message = "Numer sali może mieć maksymalnie 20 znaków")
    @Column(name = "numer_sali", nullable = false, unique = true, length = 20)
    private String numerSali;

    @NotNull(message = "Pojemność jest wymagana")
    @PositiveOrZero(message = "Pojemność nie może być ujemna")
    @Max(value = 500, message = "Pojemność nie może przekraczać 500")
    @Column(name = "pojemnosc", nullable = false)
    private Integer pojemnosc;

    @NotNull(message = "Typ sali jest wymagany")
    @Enumerated(EnumType.STRING)
    @Column(name = "typ_sali", nullable = false, length = 30)
    private TypSali typSali;

    public enum TypSali {
        WYKLADOWA, LABORATORYJNA, KOMPUTEROWA, SEMINARYJNA, INNA
    }

    public Sala() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumerSali() { return numerSali; }
    public void setNumerSali(String numerSali) { this.numerSali = numerSali; }
    public Integer getPojemnosc() { return pojemnosc; }
    public void setPojemnosc(Integer pojemnosc) { this.pojemnosc = pojemnosc; }
    public TypSali getTypSali() { return typSali; }
    public void setTypSali(TypSali typSali) { this.typSali = typSali; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sala)) return false;
        Sala sala = (Sala) o;
        return id != null && Objects.equals(id, sala.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Sala{" +
                "id=" + id +
                ", numerSali='" + numerSali + '\'' +
                ", pojemnosc=" + pojemnosc +
                ", typSali=" + typSali +
                '}';
    }
}