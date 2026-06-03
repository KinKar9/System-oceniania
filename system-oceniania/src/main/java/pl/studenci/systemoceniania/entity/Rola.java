package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "ROLE")
public class Rola {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROLI")
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "NAZWA_ROLI", nullable = false, unique = true)
    private String nazwaRoli;

    public Rola() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwaRoli() { return nazwaRoli; }
    public void setNazwaRoli(String nazwaRoli) { this.nazwaRoli = nazwaRoli; }
}