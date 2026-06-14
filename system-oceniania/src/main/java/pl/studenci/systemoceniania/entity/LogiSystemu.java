package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "LOGI_SYSTEMU", indexes = {
        @Index(name = "idx_logi_username", columnList = "username"),
        @Index(name = "idx_logi_data_akcji", columnList = "dataAkcji")
})
public class LogiSystemu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOGU", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(max = 50, message = "Nazwa użytkownika może mieć maksymalnie 50 znaków")
    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Akcja jest wymagana")
    @Size(max = 200, message = "Opis akcji może mieć maksymalnie 200 znaków")
    @Column(name = "AKCJA", nullable = false, length = 200)
    private String akcja;

    @NotNull(message = "Data akcji jest wymagana")
    @Column(name = "DATA_AKCJI", nullable = false)
    private LocalDateTime dataAkcji;

    @Size(max = 45, message = "Adres IP może mieć maksymalnie 45 znaków")
    @Column(name = "IP_ADRES", length = 45)
    private String ipAdres;

    // Opcjonalnie: enum dla typów akcji
    public enum TypAkcji {
        LOGIN, LOGOUT, DELETE, UPDATE, INSERT, SELECT, ERROR, OTHER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TYP_AKCJI", length = 20)
    private TypAkcji typAkcji;

    public LogiSystemu() {}

    @PrePersist
    protected void onCreate() {
        if (dataAkcji == null) {
            dataAkcji = LocalDateTime.now();
        }
    }

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAkcja() { return akcja; }
    public void setAkcja(String akcja) { this.akcja = akcja; }
    public LocalDateTime getDataAkcji() { return dataAkcji; }
    public void setDataAkcji(LocalDateTime dataAkcji) { this.dataAkcji = dataAkcji; }
    public String getIpAdres() { return ipAdres; }
    public void setIpAdres(String ipAdres) { this.ipAdres = ipAdres; }
    public TypAkcji getTypAkcji() { return typAkcji; }
    public void setTypAkcji(TypAkcji typAkcji) { this.typAkcji = typAkcji; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogiSystemu)) return false;
        LogiSystemu that = (LogiSystemu) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "LogiSystemu{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", akcja='" + akcja + '\'' +
                ", dataAkcji=" + dataAkcji +
                ", ipAdres='" + ipAdres + '\'' +
                ", typAkcji=" + typAkcji +
                '}';
    }
}