package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Sala;
import pl.studenci.systemoceniania.repository.SalaRepository;
import java.util.List;

@Service
public class SalaService {
    private final SalaRepository repository;

    public SalaService(SalaRepository repository) { this.repository = repository; }

    public List<Sala> findAll() { return repository.findAll(); }
    public Sala findById(Long id) { return repository.findById(id).orElseThrow(); }
    public Sala save(Sala s) { return repository.save(s); }
    public void delete(Long id) { repository.deleteById(id); }
}