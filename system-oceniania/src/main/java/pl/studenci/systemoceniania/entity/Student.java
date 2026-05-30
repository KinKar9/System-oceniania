package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "STUDENCI")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_STUDENTA")
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "IMIE")
    private String imie;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "NAZWISKO")
    private String nazwisko;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    @Column(name = "NR_INDEKSU")
    private String nrIndeksu;

    @NotBlank
    @Email
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    @Past
    @Column(name = "DATA_URODZENIA")
    private LocalDate dataUrodzenia;

    // gettery i settery
}