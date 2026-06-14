package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ZAPISY",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"ID_STUDENTA", "ID_GRUPY"})})
public class Zapisy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ZAPISU", updatable = false)
    private Long id;

    @NotNull(message = "Student jest wymagany")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STUDENTA", nullable = false)
    private Student student;

    @NotNull(message = "Grupa jest wymagana")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPY", nullable = false)
    private Grupa grupa;

    @PastOrPresent(message = "Data zapisu nie może być z przyszłości")
    @Column(name = "DATA_ZAPISU", nullable = false)
    private LocalDate dataZapisu = LocalDate.now();

    @NotNull(message = "Status jest wymagany")
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusZapisu status = StatusZapisu.AKTYWNY;

    @OneToMany(mappedBy = "zapis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ocena> oceny = new ArrayList<>();

    @Version
    @Column(name = "WERSJA")
    private Integer version;

    // Enum dla statusu
    public enum StatusZapisu {
        AKTYWNY, ZAKONCZONY, ANULOWANY
    }

    public Zapisy() {}

    // Metody pomocnicze do zarządzania kolekcją ocen
    public void addOcena(Ocena ocena) {
        oceny.add(ocena);
        ocena.setZapis(this);
    }

    public void removeOcena(Ocena ocena) {
        oceny.remove(ocena);
        ocena.setZapis(null);
    }

    // gettery i settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Grupa getGrupa() { return grupa; }
    public void setGrupa(Grupa grupa) { this.grupa = grupa; }
    public LocalDate getDataZapisu() { return dataZapisu; }
    public void setDataZapisu(LocalDate dataZapisu) { this.dataZapisu = dataZapisu; }
    public StatusZapisu getStatus() { return status; }
    public void setStatus(StatusZapisu status) { this.status = status; }
    public List<Ocena> getOceny() { return oceny; }
    public void setOceny(List<Ocena> oceny) { this.oceny = oceny; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zapisy)) return false;
        Zapisy zapisy = (Zapisy) o;
        return id != null && Objects.equals(id, zapisy.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Zapisy{" +
                "id=" + id +
                ", dataZapisu=" + dataZapisu +
                ", status=" + status +
                '}';
    }
}