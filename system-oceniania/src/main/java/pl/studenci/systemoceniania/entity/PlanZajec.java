package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plany_zajec")
public class PlanZajec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String nazwa;

    @ManyToOne
    @JoinColumn(name = "pracownik_id", nullable = false)
    private Uzytkownik pracownik;

    @ManyToOne
    @JoinColumn(name = "semestr_id")
    private Semestr semestr;

    @Column(name = "data_utworzenia", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dataUtworzenia;

    @Column(name = "aktywny")
    private boolean aktywny = true;

    @OneToMany(mappedBy = "planZajec", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PozycjaPlanu> pozycje = new ArrayList<>();

    // ========== GETTERY I SETTERY ==========
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    public Uzytkownik getPracownik() { return pracownik; }
    public void setPracownik(Uzytkownik pracownik) { this.pracownik = pracownik; }

    public Semestr getSemestr() { return semestr; }
    public void setSemestr(Semestr semestr) { this.semestr = semestr; }

    public LocalDateTime getDataUtworzenia() { return dataUtworzenia; }
    public void setDataUtworzenia(LocalDateTime dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }

    public boolean isAktywny() { return aktywny; }
    public void setAktywny(boolean aktywny) { this.aktywny = aktywny; }

    public List<PozycjaPlanu> getPozycje() { return pozycje; }
    public void setPozycje(List<PozycjaPlanu> pozycje) { this.pozycje = pozycje; }

    // ========== METODY POMOCNICZE ==========
    public void addPozycja(PozycjaPlanu pozycja) {
        pozycje.add(pozycja);
        pozycja.setPlanZajec(this);
    }

    public void removePozycja(PozycjaPlanu pozycja) {
        pozycje.remove(pozycja);
        pozycja.setPlanZajec(null);
    }
}