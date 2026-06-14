package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "SALE")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SALI", updatable = false)
    private Long id;

    @NotBlank(message = "Numer sali jest wymagany")
    @Size(max = 20, message = "Numer sali może mieć maksymalnie 20 znaków")
    @Column(name = "NUMER_SALI", nullable = false, unique = true, length = 20)
    private String numerSali;

    @NotNull(message = "Pojemność jest wymagana")
    @PositiveOrZero(message = "Pojemność nie może być ujemna")
    @Max(value = 500, message = "Pojemność nie może przekraczać 500")
    @Column(name = "POJEMNOSC", nullable = false)
    private Integer pojemnosc;

    @Size(max = 30, message = "Typ sali może mieć maksymalnie 30 znaków")
    @Column(name = "TYP_SALI", length = 30)
    private String typSali;

    // Opcjonalnie: enum dla typów sali
    public enum TypSali {
        WYKLADOWA, LABORATORYJNA, KOMPUTEROWA, SEMINARYJNA, INNA
    }

    public Sala() {}

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumerSali() { return numerSali; }
    public void setNumerSali(String numerSali) { this.numerSali = numerSali; }
    public Integer getPojemnosc() { return pojemnosc; }
    public void setPojemnosc(Integer pojemnosc) { this.pojemnosc = pojemnosc; }
    public String getTypSali() { return typSali; }
    public void setTypSali(String typSali) { this.typSali = typSali; }

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
                ", typSali='" + typSali + '\'' +
                '}';
    }
}