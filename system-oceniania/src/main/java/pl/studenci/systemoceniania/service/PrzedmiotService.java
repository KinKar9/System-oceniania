package pl.studenci.systemoceniania.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.studenci.systemoceniania.entity.Przedmiot;
import pl.studenci.systemoceniania.repository.PrzedmiotRepository;
import java.util.List;

@Service
@Transactional
public class PrzedmiotService {
    private final PrzedmiotRepository repository;

    public PrzedmiotService(PrzedmiotRepository repository) {
        this.repository = repository;
    }

    public List<Przedmiot> findAll() {
        return repository.findAll();
    }

    public Przedmiot findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono przedmiotu o ID: " + id));
    }

    public Przedmiot save(Przedmiot przedmiot) {
        return repository.save(przedmiot);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}