package pl.studenci.systemoceniania.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.SlownikOcen;
import pl.studenci.systemoceniania.repository.SlownikOcenRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SlownikOcenService {

    private final SlownikOcenRepository repository;

    public SlownikOcenService(SlownikOcenRepository repository) {
        this.repository = repository;
    }

    public List<SlownikOcen> findAll() {
        return repository.findAll();
    }
}