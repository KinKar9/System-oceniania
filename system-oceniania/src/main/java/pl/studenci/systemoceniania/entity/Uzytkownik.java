package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "UZYTKOWNICY")
public class Uzytkownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UZYTKOWNIKA")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(min = 4)
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "CZY_AKTYWNY")
    private String czyAktywny = "T";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UZYTKOWNICY_ROLE",
            joinColumns = @JoinColumn(name = "ID_UZYTKOWNIKA"),
            inverseJoinColumns = @JoinColumn(name = "ID_ROLI"))
    private Set<Rola> role = new HashSet<>();

    public Uzytkownik() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCzyAktywny() { return czyAktywny; }
    public void setCzyAktywny(String czyAktywny) { this.czyAktywny = czyAktywny; }
    public Set<Rola> getRole() { return role; }
    public void setRole(Set<Rola> role) { this.role = role; }
}