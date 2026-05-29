package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KIERUNKI")
public class Kierunek {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nazwa;

    @OneToMany(mappedBy = "kierunek", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Przedmiot> przedmioty = new ArrayList<>();

    public Kierunek() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }
    public List<Przedmiot> getPrzedmioty() { return przedmioty; }
    public void setPrzedmioty(List<Przedmiot> przedmioty) { this.przedmioty = przedmioty; }
}