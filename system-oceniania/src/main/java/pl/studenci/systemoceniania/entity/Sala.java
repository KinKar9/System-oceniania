package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SALE")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NUMER_SALI", nullable = false, unique = true)
    private String numerSali;
    private Integer pojemnosc;

    public Sala() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumerSali() { return numerSali; }
    public void setNumerSali(String numerSali) { this.numerSali = numerSali; }
    public Integer getPojemnosc() { return pojemnosc; }
    public void setPojemnosc(Integer pojemnosc) { this.pojemnosc = pojemnosc; }
}