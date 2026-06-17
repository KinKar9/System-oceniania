package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import pl.studenci.systemoceniania.enums.DzienTygodnia;

import java.time.LocalTime;

@Entity
@Table(name = "pozycje_planu")
public class PozycjaPlanu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "plan_zajec_id", nullable = false)
    private PlanZajec planZajec;

    @ManyToOne
    @JoinColumn(name = "przedmiot_id", nullable = false)
    private Przedmiot przedmiot;

    @ManyToOne
    @JoinColumn(name = "prowadzacy_id", nullable = false)
    private Uzytkownik prowadzacy;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne
    @JoinColumn(name = "grupa_id")
    private Grupa grupa;

    @Enumerated(EnumType.STRING)
    @Column(name = "dzien_tygodnia", nullable = false)
    private DzienTygodnia dzienTygodnia;

    @Column(name = "godzina_rozpoczecia", nullable = false)
    private LocalTime godzinaRozpoczecia;

    @Column(name = "godzina_zakonczenia", nullable = false)
    private LocalTime godzinaZakonczenia;

    // ========== GETTERY I SETTERY ==========
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PlanZajec getPlanZajec() { return planZajec; }
    public void setPlanZajec(PlanZajec planZajec) { this.planZajec = planZajec; }

    public Przedmiot getPrzedmiot() { return przedmiot; }
    public void setPrzedmiot(Przedmiot przedmiot) { this.przedmiot = przedmiot; }

    public Uzytkownik getProwadzacy() { return prowadzacy; }
    public void setProwadzacy(Uzytkownik prowadzacy) { this.prowadzacy = prowadzacy; }

    public Sala getSala() { return sala; }
    public void setSala(Sala sala) { this.sala = sala; }

    public Grupa getGrupa() { return grupa; }
    public void setGrupa(Grupa grupa) { this.grupa = grupa; }

    public DzienTygodnia getDzienTygodnia() { return dzienTygodnia; }
    public void setDzienTygodnia(DzienTygodnia dzienTygodnia) { this.dzienTygodnia = dzienTygodnia; }

    public LocalTime getGodzinaRozpoczecia() { return godzinaRozpoczecia; }
    public void setGodzinaRozpoczecia(LocalTime godzinaRozpoczecia) { this.godzinaRozpoczecia = godzinaRozpoczecia; }

    public LocalTime getGodzinaZakonczenia() { return godzinaZakonczenia; }
    public void setGodzinaZakonczenia(LocalTime godzinaZakonczenia) { this.godzinaZakonczenia = godzinaZakonczenia; }

    // ========== WALIDACJA ==========
    @AssertTrue(message = "Godzina rozpoczęcia musi być przed godziną zakończenia")
    public boolean isGodzinyPoprawne() {
        return godzinaRozpoczecia != null && godzinaZakonczenia != null &&
                godzinaRozpoczecia.isBefore(godzinaZakonczenia);
    }
}