package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Przedmiot;
import pl.studenci.systemoceniania.repository.PrzedmiotRepository;
import java.util.List;

@Service
public class PrzedmiotService {
    private final PrzedmiotRepository repository;

    public PrzedmiotService(PrzedmiotRepository repository) {
        this.repository = repository;
    }

    public List<Przedmiot> findAll() {
        return repository.findAll();
    }

    public Przedmiot findById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Przedmiot save(Przedmiot przedmiot) {
        return repository.save(przedmiot);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}