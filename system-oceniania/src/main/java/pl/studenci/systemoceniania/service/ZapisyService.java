package pl.studenci.systemoceniania.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.*;
import pl.studenci.systemoceniania.repository.*;
import java.util.List;

@Service
@Transactional
public class ZapisyService {
    private final ZapisyRepository zapisyRepository;
    private final StudentRepository studentRepository;
    private final GrupaRepository grupaRepository;

    public ZapisyService(ZapisyRepository zapisyRepository, StudentRepository studentRepository, GrupaRepository grupaRepository) {
        this.zapisyRepository = zapisyRepository;
        this.studentRepository = studentRepository;
        this.grupaRepository = grupaRepository;
    }

    public List<Zapisy> findAll() { return zapisyRepository.findAll(); }
    public Zapisy findById(Long id) { return zapisyRepository.findById(id).orElseThrow(); }

    public Zapisy zapiszStudenta(Long studentId, Long grupaId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Grupa grupa = grupaRepository.findById(grupaId).orElseThrow();
        if (zapisyRepository.findByStudentIdAndGrupaId(studentId, grupaId).isPresent()) {
            throw new IllegalStateException("Student już zapisany do tej grupy");
        }
        Zapisy zapis = new Zapisy();
        zapis.setStudent(student);
        zapis.setGrupa(grupa);
        return zapisyRepository.save(zapis);
    }

    public void wypiszStudenta(Long zapisId) {
        Zapisy zapis = findById(zapisId);
        zapis.setStatus("Wypisany");
        zapisyRepository.save(zapis);
    }
}