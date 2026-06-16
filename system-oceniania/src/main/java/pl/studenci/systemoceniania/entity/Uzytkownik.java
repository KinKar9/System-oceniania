package pl.studenci.systemoceniania.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// DODANE IMPORTY:
import pl.studenci.systemoceniania.entity.Rola;
import pl.studenci.systemoceniania.entity.Student;

@Entity
@Table(name = "uzytkownicy")
public class Uzytkownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_uzytkownika", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 50, message = "Nazwa musi mieć od 3 do 50 znaków")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 4, message = "Hasło musi mieć co najmniej 4 znaki")
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    @Size(max = 100, message = "Email może mieć maksymalnie 100 znaków")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotNull(message = "Pole aktywności jest wymagane")
    @Column(name = "czy_aktywny", nullable = false)
    private boolean czyAktywny = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "uzytkownicy_role",
            joinColumns = @JoinColumn(name = "id_uzytkownika"),
            inverseJoinColumns = @JoinColumn(name = "id_roli"))
    @JsonIgnoreProperties("uzytkownicy")
    private Set<Rola> role = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_studenta", nullable = true)
    @JsonIgnore
    private Student student;

    @CreationTimestamp
    @Column(name = "data_utworzenia", updatable = false)
    private LocalDateTime dataUtworzenia;

    @UpdateTimestamp
    @Column(name = "data_aktualizacji")
    private LocalDateTime dataAktualizacji;

    public Uzytkownik() {}

    // gettery i settery (bez zmian)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isCzyAktywny() { return czyAktywny; }
    public void setCzyAktywny(boolean czyAktywny) { this.czyAktywny = czyAktywny; }

    public Set<Rola> getRole() { return role; }
    public void setRole(Set<Rola> role) { this.role = role; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDateTime getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(LocalDateTime dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }

    public LocalDateTime getDataAktualizacji() { return dataAktualizacji; }
    public void setDataAktualizacji(LocalDateTime dataAktualizacji) { this.dataAktualizacji = dataAktualizacji; }

    public void addRole(Rola rola) {
        role.add(rola);
        rola.getUzytkownicy().add(this);
    }

    public void removeRole(Rola rola) {
        role.remove(rola);
        rola.getUzytkownicy().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Uzytkownik)) return false;
        Uzytkownik that = (Uzytkownik) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Uzytkownik{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", czyAktywny=" + czyAktywny +
                '}';
    }
}