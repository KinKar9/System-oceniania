package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import pl.studenci.systemoceniania.entity.Grupa;

@Entity
@Table(name = "pracownicy")
public class Pracownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pracownika", updatable = false)
    private Long id;

    @NotBlank(message = "Imię jest wymagane")
    @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków")
    @Column(name = "imie", nullable = false, length = 50)
    private String imie;

    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(max = 100, message = "Nazwisko może mieć maksymalnie 100 znaków")
    @Column(name = "nazwisko", nullable = false, length = 100)
    private String nazwisko;

    @Size(min = 2, max = 50, message = "Tytuł naukowy może mieć od 2 do 50 znaków")
    @Column(name = "tytul_naukowy", length = 50)
    private String tytulNaukowy;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    @Size(max = 100, message = "Email może mieć maksymalnie 100 znaków")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @OneToMany(mappedBy = "pracownik", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Grupa> grupy = new ArrayList<>();

    @Version
    @Column(name = "wersja")
    private Integer version;

    public Pracownik() {}

    public void addGrupa(Grupa grupa) {
        grupy.add(grupa);
        grupa.setPracownik(this);
    }

    public void removeGrupa(Grupa grupa) {
        grupy.remove(grupa);
        grupa.setPracownik(null);
    }

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
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pracownik)) return false;
        Pracownik that = (Pracownik) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Pracownik{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", tytulNaukowy='" + tytulNaukowy + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}