package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PRZEDMIOTY")
public class Przedmiot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "KOD_PRZEDMIOTU", nullable = false, unique = true)
    private String kodPrzedmiotu;

    @Column(nullable = false)
    private String nazwa;

    @ManyToOne
    @JoinColumn(name = "KIERUNEK_ID", nullable = false)
    private Kierunek kierunek;

    public Przedmiot() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKodPrzedmiotu() { return kodPrzedmiotu; }
    public void setKodPrzedmiotu(String kodPrzedmiotu) { this.kodPrzedmiotu = kodPrzedmiotu; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public Kierunek getKierunek() { return kierunek; }
    public void setKierunek(Kierunek kierunek) { this.kierunek = kierunek; }
}