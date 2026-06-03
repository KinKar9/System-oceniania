package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "WARUNKI_ZAL")
public class WarunkiZal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_WARUNKU")
    private Long id;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "WYMAGANA_PROCENT", nullable = false)
    private Integer wymaganaProcent;

    @Column(name = "MINIMALNA_SREDNIA")
    private Double minimalnaSrednia = 3.0;

    @Column(name = "CZY_WYMAGANY_EGZAMIN")
    private String czyWymaganyEgzamin = "N";

    @OneToOne
    @JoinColumn(name = "ID_PRZEDMIOTU", nullable = false)
    private Przedmiot przedmiot;

    public WarunkiZal() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getWymaganaProcent() { return wymaganaProcent; }
    public void setWymaganaProcent(Integer wymaganaProcent) { this.wymaganaProcent = wymaganaProcent; }
    public Double getMinimalnaSrednia() { return minimalnaSrednia; }
    public void setMinimalnaSrednia(Double minimalnaSrednia) { this.minimalnaSrednia = minimalnaSrednia; }
    public String getCzyWymaganyEgzamin() { return czyWymaganyEgzamin; }
    public void setCzyWymaganyEgzamin(String czyWymaganyEgzamin) { this.czyWymaganyEgzamin = czyWymaganyEgzamin; }
    public Przedmiot getPrzedmiot() { return przedmiot; }
    public void setPrzedmiot(Przedmiot przedmiot) { this.przedmiot = przedmiot; }
}