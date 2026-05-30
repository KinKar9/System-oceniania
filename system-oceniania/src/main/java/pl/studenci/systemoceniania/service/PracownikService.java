package pl.studenci.systemoceniania.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Pracownik;
import pl.studenci.systemoceniania.repository.PracownikRepository;
import java.util.List;

@Transactional
@Service
public class PracownikService {
    private final PracownikRepository repository;

    public PracownikService(PracownikRepository repository) { this.repository = repository; }

    public List<Pracownik> findAll() { return repository.findAll(); }
    public Pracownik findById(Long id) { return repository.findById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Nie znaleziono obiektu")); }
    public Pracownik save(Pracownik p) { return repository.save(p); }
    public void delete(Long id) { repository.deleteById(id); }
}