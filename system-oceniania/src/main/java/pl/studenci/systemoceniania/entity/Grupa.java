package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GRUPY")
public class Grupa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPY")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "NAZWA_GRUPY", nullable = false)
    private String nazwaGrupy;

    @Column(name = "LIMIT_MIEJSC")
    private Integer limitMiejsc;

    @ManyToOne
    @JoinColumn(name = "ID_PRZEDMIOTU", nullable = false)
    private Przedmiot przedmiot;

    @ManyToOne
    @JoinColumn(name = "ID_PRACOWNIKA", nullable = false)
    private Pracownik pracownik;

    @OneToMany(mappedBy = "grupa", cascade = CascadeType.ALL)
    private List<Zapisy> zapisy = new ArrayList<>();

    public Grupa() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazwaGrupy() { return nazwaGrupy; }
    public void setNazwaGrupy(String nazwaGrupy) { this.nazwaGrupy = nazwaGrupy; }
    public Integer getLimitMiejsc() { return limitMiejsc; }
    public void setLimitMiejsc(Integer limitMiejsc) { this.limitMiejsc = limitMiejsc; }
    public Przedmiot getPrzedmiot() { return przedmiot; }
    public void setPrzedmiot(Przedmiot przedmiot) { this.przedmiot = przedmiot; }
    public Pracownik getPracownik() { return pracownik; }
    public void setPracownik(Pracownik pracownik) { this.pracownik = pracownik; }
    public List<Zapisy> getZapisy() { return zapisy; }
    public void setZapisy(List<Zapisy> zapisy) { this.zapisy = zapisy; }
}