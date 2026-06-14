package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "RANKINGI", indexes = {
        @Index(name = "idx_ranking_semestr", columnList = "semestr_id"),
        @Index(name = "idx_ranking_data", columnList = "dataGenerowania")
})
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RANKINGU", updatable = false)
    private Long id;

    @NotNull(message = "Semestr jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEMESTR_ID", nullable = false)
    private Semestr semestr;

    @Column(name = "DATA_GENEROWANIA", nullable = false, updatable = false)
    private LocalDateTime dataGenerowania;

    @Column(name = "DANE_RANKINGU", columnDefinition = "CLOB")
    private String daneRankingu;

    public Ranking() {}

    @PrePersist
    protected void onCreate() {
        if (dataGenerowania == null) {
            dataGenerowania = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Semestr getSemestr() { return semestr; }
    public void setSemestr(Semestr semestr) { this.semestr = semestr; }
    public LocalDateTime getDataGenerowania() { return dataGenerowania; }
    public void setDataGenerowania(LocalDateTime dataGenerowania) { this.dataGenerowania = dataGenerowania; }
    public String getDaneRankingu() { return daneRankingu; }
    public void setDaneRankingu(String daneRankingu) { this.daneRankingu = daneRankingu; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ranking)) return false;
        Ranking ranking = (Ranking) o;
        return id != null && Objects.equals(id, ranking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "id=" + id +
                ", dataGenerowania=" + dataGenerowania +
                '}';
    }
}