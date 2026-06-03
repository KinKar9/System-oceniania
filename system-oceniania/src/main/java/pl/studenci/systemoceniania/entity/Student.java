package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "STUDENCI")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_STUDENTA")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "IMIE", nullable = false)
    private String imie;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NAZWISKO", nullable = false)
    private String nazwisko;

    @NotBlank
    @Size(min = 6, max = 10)
    @Column(name = "NR_INDEKSU", nullable = false, unique = true)
    private String nrIndeksu;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @NotNull
    @Past
    @Column(name = "DATA_URODZENIA", nullable = false)
    private LocalDate dataUrodzenia;

    @Size(min = 11, max = 11)
    @Column(name = "PESEL", unique = true)
    private String pesel;

    @Column(name = "SECURE_TOKEN", unique = true)
    private String secureToken;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Zapisy> zapisy = new ArrayList<>();

    public Student() {}
    // gettery i settery (wszystkie)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }
    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public String getNrIndeksu() { return nrIndeksu; }
    public void setNrIndeksu(String nrIndeksu) { this.nrIndeksu = nrIndeksu; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDataUrodzenia() { return dataUrodzenia; }
    public void setDataUrodzenia(LocalDate dataUrodzenia) { this.dataUrodzenia = dataUrodzenia; }
    public String getPesel() { return pesel; }
    public void setPesel(String pesel) { this.pesel = pesel; }
    public String getSecureToken() { return secureToken; }
    public void setSecureToken(String secureToken) { this.secureToken = secureToken; }
    public List<Zapisy> getZapisy() { return zapisy; }
    public void setZapisy(List<Zapisy> zapisy) { this.zapisy = zapisy; }
}