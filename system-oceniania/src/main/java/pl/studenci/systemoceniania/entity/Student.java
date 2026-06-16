package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import pl.studenci.systemoceniania.entity.Zapisy;

@Entity
@Table(name = "studenci")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_studenta", updatable = false)
    private Long id;

    @NotBlank(message = "Imię jest wymagane")
    @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków")
    @Column(name = "imie", nullable = false, length = 50)
    private String imie;

    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(max = 100, message = "Nazwisko może mieć maksymalnie 100 znaków")
    @Column(name = "nazwisko", nullable = false, length = 100)
    private String nazwisko;

    @NotBlank(message = "Numer indeksu jest wymagany")
    @Size(min = 6, max = 10, message = "Numer indeksu musi mieć od 6 do 10 znaków")
    @Column(name = "nr_indeksu", nullable = false, unique = true, length = 10)
    private String nrIndeksu;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    @Size(max = 100, message = "Email może mieć maksymalnie 100 znaków")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotNull(message = "Data urodzenia jest wymagana")
    @Past(message = "Data urodzenia musi być w przeszłości")
    @Column(name = "data_urodzenia", nullable = false)
    private LocalDate dataUrodzenia;

    @Pattern(regexp = "^[0-9]{11}$", message = "PESEL musi składać się z 11 cyfr")
    @Column(name = "pesel", unique = true, length = 11)
    private String pesel;

    @Size(max = 255, message = "Token może mieć maksymalnie 255 znaków")
    @Column(name = "secure_token", unique = true, length = 255)
    private String secureToken;

    @CreationTimestamp
    @Column(name = "data_utworzenia", updatable = false)
    private LocalDateTime dataUtworzenia;

    @UpdateTimestamp
    @Column(name = "data_aktualizacji")
    private LocalDateTime dataAktualizacji;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zapisy> zapisy = new ArrayList<>();

    public Student() {}

    public void addZapis(Zapisy zapis) {
        zapisy.add(zapis);
        zapis.setStudent(this);
    }

    public void removeZapis(Zapisy zapis) {
        zapisy.remove(zapis);
        zapis.setStudent(null);
    }

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
    public LocalDateTime getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(LocalDateTime dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }
    public LocalDateTime getDataAktualizacji() { return dataAktualizacji; }
    public void setDataAktualizacji(LocalDateTime dataAktualizacji) { this.dataAktualizacji = dataAktualizacji; }
    public List<Zapisy> getZapisy() { return zapisy; }
    public void setZapisy(List<Zapisy> zapisy) { this.zapisy = zapisy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return id != null && Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", nrIndeksu='" + nrIndeksu + '\'' +
                ", email='" + email + '\'' +
                ", dataUrodzenia=" + dataUrodzenia +
                '}';
    }
    @Column(name = "CZY_AKTYWNY", nullable = false)
    private boolean czyAktywny = true;
    public boolean isCzyAktywny() {
        return czyAktywny;
    }
    public void setCzyAktywny(boolean czyAktywny) {
        this.czyAktywny = czyAktywny;
    }
}