package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "LOGI_SYSTEMU")
public class LogiSystemu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LOGU")
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "AKCJA", nullable = false)
    private String akcja;

    @Column(name = "DATA_AKCJI")
    private LocalDate dataAkcji = LocalDate.now();

    @Column(name = "IP_ADRES")
    private String ipAdres;

    public LogiSystemu() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAkcja() { return akcja; }
    public void setAkcja(String akcja) { this.akcja = akcja; }
    public LocalDate getDataAkcji() { return dataAkcji; }
    public void setDataAkcji(LocalDate dataAkcji) { this.dataAkcji = dataAkcji; }
    public String getIpAdres() { return ipAdres; }
    public void setIpAdres(String ipAdres) { this.ipAdres = ipAdres; }
}