package pl.studenci.systemoceniania.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ZAPISY",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"ID_STUDENTA", "ID_GRUPY"})})
public class Zapisy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ZAPISU")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_STUDENTA", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "ID_GRUPY", nullable = false)
    private Grupa grupa;

    @Column(name = "DATA_ZAPISU")
    private LocalDate dataZapisu = LocalDate.now();

    @Size(max = 20)
    @Column(name = "STATUS")
    private String status = "Aktywny";

    @OneToMany(mappedBy = "zapis", cascade = CascadeType.ALL)
    private List<Ocena> oceny = new ArrayList<>();

    public Zapisy() {}
    // gettery, settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Grupa getGrupa() { return grupa; }
    public void setGrupa(Grupa grupa) { this.grupa = grupa; }
    public LocalDate getDataZapisu() { return dataZapisu; }
    public void setDataZapisu(LocalDate dataZapisu) { this.dataZapisu = dataZapisu; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Ocena> getOceny() { return oceny; }
    public void setOceny(List<Ocena> oceny) { this.oceny = oceny; }
}
