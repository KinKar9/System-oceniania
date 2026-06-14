package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "GRUPY", indexes = {
        @Index(name = "idx_grupy_przedmiot", columnList = "id_przedmiotu"),
        @Index(name = "idx_grupy_pracownik", columnList = "id_pracownika")
})
public class Grupa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPY", updatable = false)
    private Long id;

    @NotBlank(message = "Nazwa grupy jest wymagana")
    @Size(max = 50, message = "Nazwa grupy może mieć maksymalnie 50 znaków")
    @Column(name = "NAZWA_GRUPY", nullable = false, length = 50)
    private String nazwaGrupy;

    @PositiveOrZero(message = "Limit miejsc nie może być ujemny")
    @Column(name = "LIMIT_MIEJSC")
    private Integer limitMiejsc;

    @NotNull(message = "Przedmiot jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRZEDMIOTU", nullable = false)
    private Przedmiot przedmiot;

    @NotNull(message = "Pracownik jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRACOWNIKA", nullable = false)
    private Pracownik pracownik;

    // Cascade tylko PERSIST i MERGE – bez REMOVE (bezpieczniej)
    @OneToMany(mappedBy = "grupa", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Zapisy> zapisy = new ArrayList<>();

    public Grupa() {}

    // Metody pomocnicze do zarządzania relacją z zapisami
    public void addZapis(Zapisy zapis) {
        zapisy.add(zapis);
        zapis.setGrupa(this);
    }

    public void removeZapis(Zapisy zapis) {
        zapisy.remove(zapis);
        zapis.setGrupa(null);
    }

    // gettery i settery
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