package pl.studenci.systemoceniania.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.studenci.systemoceniania.entity.Sala;
import pl.studenci.systemoceniania.repository.SalaRepository;
import java.util.List;

@Service
@Transactional
public class SalaService {
    private final SalaRepository repository;

    public SalaService(SalaRepository repository) { this.repository = repository; }

    public List<Sala> findAll() { return repository.findAll(); }
    public Sala findById(Long id) { return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono sali o ID: " + id)); }
    public Sala save(Sala s) { return repository.save(s); }
    public void delete(Long id) { repository.deleteById(id); }
}