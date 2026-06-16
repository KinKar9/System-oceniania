package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;
import pl.studenci.systemoceniania.entity.Ocena;

@Entity
@Table(name = "historia_ocen")
public class HistoriaOcen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historii", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_oceny", nullable = false)
    private Ocena ocena;

    @Column(name = "stara_wartosc")
    private Double staraWartosc;

    @Column(name = "nowa_wartosc")
    private Double nowaWartosc;

    @Column(name = "data_modyfikacji", nullable = false)
    private LocalDate dataModyfikacji;

    @Size(max = 50)
    @Column(name = "uzytkownik", length = 50)
    private String uzytkownik;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operacja", nullable = false, length = 20)
    private Operacja operacja;

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