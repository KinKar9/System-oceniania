package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PRACOWNICY")
public class Pracownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imie;
    private String nazwisko;
    @Column(name = "TYTUL_NAUKOWY")
    private String tytulNaukowy;

    public Pracownik() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public String getTytulNaukowy() { return tytulNaukowy; }
    public void setTytulNaukowy(String tytulNaukowy) { this.tytulNaukowy = tytulNaukowy; }
}