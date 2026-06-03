package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "RANKINGI")
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RANKINGU")
    private Long id;

    @Column(name = "ID_SEMESTRU")
    private String idSemestru;

    @Column(name = "DATA_GENEROWANIA")
    private LocalDate dataGenerowania = LocalDate.now();

    @Lob
    @Column(name = "DANE_RANKINGU", columnDefinition = "CLOB")
    private String daneRankingu;

    public Ranking() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdSemestru() { return idSemestru; }
    public void setIdSemestru(String idSemestru) { this.idSemestru = idSemestru; }
    public LocalDate getDataGenerowania() { return dataGenerowania; }
    public void setDataGenerowania(LocalDate dataGenerowania) { this.dataGenerowania = dataGenerowania; }
    public String getDaneRankingu() { return daneRankingu; }
    public void setDaneRankingu(String daneRankingu) { this.daneRankingu = daneRankingu; }
}