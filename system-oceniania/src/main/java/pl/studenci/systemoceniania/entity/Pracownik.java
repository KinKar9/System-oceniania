package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRACOWNICY")
public class Pracownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRACOWNIKA")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "IMIE", nullable = false)
    private String imie;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NAZWISKO", nullable = false)
    private String nazwisko;

    @Size(max = 50)
    @Column(name = "TYTUL_NAUKOWY")
    private String tytulNaukowy;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "pracownik")
    private List<Grupa> grupy = new ArrayList<>();

    public Pracownik() {}
    // gettery i settery (wszystkie pola)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public String getTytulNaukowy() { return tytulNaukowy; }
    public void setTytulNaukowy(String tytulNaukowy) { this.tytulNaukowy = tytulNaukowy; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Grupa> getGrupy() { return grupy; }
    public void setGrupy(List<Grupa> grupy) { this.grupy = grupy; }
}