package pl.studenci.systemoceniania.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// BRAKUJĄCY IMPORT:
import pl.studenci.systemoceniania.entity.Zapisy;

@Entity
@Table(name = "grupy", indexes = {
        @Index(name = "idx_grupy_przedmiot", columnList = "id_przedmiotu"),
        @Index(name = "idx_grupy_pracownik", columnList = "id_pracownika")
})
public class Grupa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grupy_seq")
    @SequenceGenerator(name = "grupy_seq", sequenceName = "grupy_seq", allocationSize = 1)
    @Column(name = "id_grupy", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa grupy jest wymagana")
    @Size(max = 50, message = "Nazwa grupy może mieć maksymalnie 50 znaków")
    @Column(name = "nazwa_grupy", nullable = false, length = 50)
    private String nazwaGrupy;

    @PositiveOrZero(message = "Limit miejsc nie może być ujemny")
    @Column(name = "limit_miejsc")
    private Integer limitMiejsc;

    @NotNull(message = "Przedmiot jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_przedmiotu", nullable = false)
    private Przedmiot przedmiot;

    @NotNull(message = "Pracownik jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracownika", nullable = false)
    private Pracownik pracownik;

    // ZAPOBIEGA CYkLICZNEJ SERIALIZACJI JSON
    @JsonIgnore
    @OneToMany(mappedBy = "grupa", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Zapisy> zapisy = new ArrayList<>();

    public Grupa() {}

    public void addZapis(Zapisy zapis) {
        zapisy.add(zapis);
        zapis.setGrupa(this);
    }

    public void removeZapis(Zapisy zapis) {
        zapisy.remove(zapis);
        zapis.setGrupa(null);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grupa)) return false;
        Grupa grupa = (Grupa) o;
        return id != null && Objects.equals(id, grupa.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Grupa{" +
                "id=" + id +
                ", nazwaGrupy='" + nazwaGrupy + '\'' +
                ", limitMiejsc=" + limitMiejsc +
                '}';
    }
}