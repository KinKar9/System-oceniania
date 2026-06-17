package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Grupa;
import pl.studenci.systemoceniania.repository.GrupaRepository;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class GrupaService {

    private final GrupaRepository repository;

    public GrupaService(GrupaRepository repository) {
        this.repository = repository;
    }

    public Grupa findById(Integer id) {
        return repository.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Grupa nie istnieje"));
    }
    @Transactional(readOnly = true)
    public List<Grupa> findAll() {
        return repository.findAll();
    }
}