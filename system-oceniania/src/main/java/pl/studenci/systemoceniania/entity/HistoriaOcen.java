package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "HISTORIA_OCEN")
public class HistoriaOcen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORII", updatable = false)
    private Long id;

    // Relacja z encją Ocena – zamiast Long
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OCENY", nullable = false)
    private Ocena ocena;

    @Column(name = "STARA_WARTOSC")
    private Double staraWartosc;

    @Column(name = "NOWA_WARTOSC")
    private Double nowaWartosc;

    @Column(name = "DATA_MODYFIKACJI", nullable = false)
    private LocalDate dataModyfikacji;

    @Size(max = 50)
    @Column(name = "UZYTKOWNIK", length = 50)
    private String uzytkownik;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "OPERACJA", nullable = false, length = 20)
    private Operacja operacja;

    // Enum dla dozwolonych operacji
    public enum Operacja {
        INSERT, UPDATE, DELETE
    }

    public HistoriaOcen() {}

    @PrePersist
    protected void onCreate() {
        if (dataModyfikacji == null) {
            dataModyfikacji = LocalDate.now();
        }
    }

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Ocena getOcena() { return ocena; }
    public void setOcena(Ocena ocena) { this.ocena = ocena; }
    public Double getStaraWartosc() { return staraWartosc; }
    public void setStaraWartosc(Double staraWartosc) { this.staraWartosc = staraWartosc; }
    public Double getNowaWartosc() { return nowaWartosc; }
    public void setNowaWartosc(Double nowaWartosc) { this.nowaWartosc = nowaWartosc; }
    public LocalDate getDataModyfikacji() { return dataModyfikacji; }
    public void setDataModyfikacji(LocalDate dataModyfikacji) { this.dataModyfikacji = dataModyfikacji; }
    public String getUzytkownik() { return uzytkownik; }
    public void setUzytkownik(String uzytkownik) { this.uzytkownik = uzytkownik; }
    public Operacja getOperacja() { return operacja; }
    public void setOperacja(Operacja operacja) { this.operacja = operacja; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoriaOcen)) return false;
        HistoriaOcen that = (HistoriaOcen) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "HistoriaOcen{" +
                "id=" + id +
                ", dataModyfikacji=" + dataModyfikacji +
                ", operacja=" + operacja +
                '}';
    }
}