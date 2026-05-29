package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GRUPY")
public class Grupa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAZWA_GRUPY", nullable = false)
    private String nazwaGrupy;

    public Grupa() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwaGrupy() { return nazwaGrupy; }
    public void setNazwaGrupy(String nazwaGrupy) { this.nazwaGrupy = nazwaGrupy; }
}