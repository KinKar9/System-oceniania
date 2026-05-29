package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.repository.PracownikRepository;
import java.util.List;

@Service
public class PracownikService {
    private final PracownikRepository repository;

    public PracownikService(PracownikRepository repository) { this.repository = repository; }

    public List<Pracownik> findAll() { return repository.findAll(); }
    public Pracownik findById(Long id) { return repository.findById(id).orElseThrow(); }
    public Pracownik save(Pracownik p) { return repository.save(p); }
    public void delete(Long id) { repository.deleteById(id); }
}