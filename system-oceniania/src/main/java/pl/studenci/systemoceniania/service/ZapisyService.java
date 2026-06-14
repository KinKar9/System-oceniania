package pl.studenci.systemoceniania.service;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.*;
import pl.studenci.systemoceniania.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ZapisyService {

    private static final Logger log = LoggerFactory.getLogger(ZapisyService.class);

    private final ZapisyRepository zapisyRepository;
    private final StudentRepository studentRepository;
    private final GrupaRepository grupaRepository;

    public ZapisyService(ZapisyRepository zapisyRepository,
                         StudentRepository studentRepository,
                         GrupaRepository grupaRepository) {
        this.zapisyRepository = zapisyRepository;
        this.studentRepository = studentRepository;
        this.grupaRepository = grupaRepository;
    }

    // Metody odczytu – mogą być readOnly
    @Transactional(readOnly = true)
    public List<Zapisy> findAll() {
        return zapisyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Zapisy> findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("Próba wyszukania zapisu z nieprawidłowym ID: {}", id);
            return Optional.empty();
        }
        return zapisyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean czyStudentZapisany(Long studentId, Long grupaId) {
        if (studentId == null || grupaId == null) {
            return false;
        }
        return zapisyRepository.findByStudentIdAndGrupaId(studentId, grupaId).isPresent();
    }

    public Zapisy zapiszStudenta(Long studentId, Long grupaId) {
        // Walidacja parametrów
        if (studentId == null || grupaId == null) {
            throw new IllegalArgumentException("ID studenta i grupy nie mogą być null");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono studenta o ID: {}", studentId);
                    return new IllegalArgumentException("Student o podanym ID nie istnieje");
                });

        Grupa grupa = grupaRepository.findById(grupaId)
                .orElseThrow(() -> {
                    log.warn("Nie znaleziono grupy o ID: {}", grupaId);
                    return new IllegalArgumentException("Grupa o podanym ID nie istnieje");
                });

        // Sprawdzenie, czy student już zapisany
        if (czyStudentZapisany(studentId, grupaId)) {
            log.warn("Student {} już zapisany do grupy {}", studentId, grupaId);
            throw new IllegalStateException("Student jest już zapisany do tej grupy");
        }

        // Sprawdzenie limitu miejsc
        Integer limit = grupa.getLimitMiejsc();
        if (limit != null && limit > 0) {
            long obecnaLiczba = zapisyRepository.countByGrupaId(grupaId);
            if (obecnaLiczba >= limit) {
                log.warn("Brak wolnych miejsc w grupie {} (limit: {}, obecnie: {})", grupaId, limit, obecnaLiczba);
                throw new IllegalStateException("Brak wolnych miejsc w grupie");
            }
        }

        try {
            Zapisy zapis = new Zapisy();
            zapis.setStudent(student);
            zapis.setGrupa(grupa);
            zapis.setStatus(Zapisy.StatusZapisu.AKTYWNY);
            Zapisy saved = zapisyRepository.save(zapis);
            log.info("Student {} zapisany do grupy {} (zapis ID: {})", studentId, grupaId, saved.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("Błąd integralności przy zapisie studenta {} do grupy {}: {}", studentId, grupaId, e.getMessage());
            throw new IllegalStateException("Nie udało się zapisać studenta – możliwy duplikat zapisu", e);
        }
    }

    public void wypiszStudenta(Long zapisId) {
        if (zapisId == null || zapisId <= 0) {
            throw new IllegalArgumentException("Nieprawidłowe ID zapisu");
        }

        Zapisy zapis = zapisyRepository.findById(zapisId)
                .orElseThrow(() -> {
                    log.warn("Próba wypisania z nieistniejącego zapisu ID: {}", zapisId);
                    return new IllegalArgumentException("Zapis o podanym ID nie istnieje");
                });
        zapis.setStatus(Zapisy.StatusZapisu.ANULOWANY);
        log.info("Student {} wypisany z grupy {} (zapis ID: {})",
                zapis.getStudent().getId(), zapis.getGrupa().getId(), zapisId);
    }
}