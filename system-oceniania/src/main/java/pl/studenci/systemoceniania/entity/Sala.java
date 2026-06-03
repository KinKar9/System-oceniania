package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "SALE")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SALI")
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "NUMER_SALI", nullable = false, unique = true)
    private String numerSali;

    @NotNull
    @Positive
    @Column(name = "POJEMNOSC", nullable = false)
    private Integer pojemnosc;

    @Size(max = 30)
    @Column(name = "TYP_SALI")
    private String typSali;

    public Sala() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumerSali() { return numerSali; }
    public void setNumerSali(String numerSali) { this.numerSali = numerSali; }
    public Integer getPojemnosc() { return pojemnosc; }
    public void setPojemnosc(Integer pojemnosc) { this.pojemnosc = pojemnosc; }
    public String getTypSali() { return typSali; }
    public void setTypSali(String typSali) { this.typSali = typSali; }
}