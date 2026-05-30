package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HISTORIA_LOGOWANIA")
public class HistoriaLogowania {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private LocalDateTime dataWylogowania;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getDataWylogowania() {
        return dataWylogowania;
    }

    public void setDataWylogowania(LocalDateTime dataWylogowania) {
        this.dataWylogowania = dataWylogowania;
    }

}