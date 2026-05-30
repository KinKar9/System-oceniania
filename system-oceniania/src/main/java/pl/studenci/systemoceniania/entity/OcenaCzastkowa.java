package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "OCENY_CZASTKOWE")
public class OcenaCzastkowa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_OCENY_CZASTKOWEJ")
    private Long id;

    @NotNull
    @DecimalMin("2.0")
    @DecimalMax("5.0")
    @Column(name = "OCENA")
    private Double ocena;

    @Size(max = 200)
    @Column(name = "KOMENTARZ")
    private String komentarz;

    // gettery i settery
}