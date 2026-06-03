package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "HISTORIA_OCEN")
public class HistoriaOcen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HISTORII")
    private Long id;

    @Column(name = "ID_OCENY", nullable = false)
    private Long idOceny;

    @Column(name = "STARA_WARTOSC")
    private Double staraWartosc;

    @Column(name = "NOWA_WARTOSC")
    private Double nowaWartosc;

    @Column(name = "DATA_MODYFIKACJI")
    private LocalDate dataModyfikacji = LocalDate.now();

    @Column(name = "UZYTKOWNIK")
    private String uzytkownik;

    @Column(name = "OPERACJA", nullable = false)
    private String operacja;

    public HistoriaOcen() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdOceny() { return idOceny; }
    public void setIdOceny(Long idOceny) { this.idOceny = idOceny; }
    public Double getStaraWartosc() { return staraWartosc; }
    public void setStaraWartosc(Double staraWartosc) { this.staraWartosc = staraWartosc; }
    public Double getNowaWartosc() { return nowaWartosc; }
    public void setNowaWartosc(Double nowaWartosc) { this.nowaWartosc = nowaWartosc; }
    public LocalDate getDataModyfikacji() { return dataModyfikacji; }
    public void setDataModyfikacji(LocalDate dataModyfikacji) { this.dataModyfikacji = dataModyfikacji; }
    public String getUzytkownik() { return uzytkownik; }
    public void setUzytkownik(String uzytkownik) { this.uzytkownik = uzytkownik; }
    public String getOperacja() { return operacja; }
    public void setOperacja(String operacja) { this.operacja = operacja; }
}