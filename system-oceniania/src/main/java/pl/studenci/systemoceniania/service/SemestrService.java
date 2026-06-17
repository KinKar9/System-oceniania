package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Semestr;
import pl.studenci.systemoceniania.repository.SemestrRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SemestrService {

    private final SemestrRepository repository;

    public SemestrService(SemestrRepository repository) {
        this.repository = repository;
    }

    public List<Semestr> findAll() {
        return repository.findAll();
    }

    public Semestr findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestr nie istnieje"));
    }
}