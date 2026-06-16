package pl.studenci.systemoceniania.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.studenci.systemoceniania.entity.Rola;
import pl.studenci.systemoceniania.entity.Student;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.repository.RolaRepository;
import pl.studenci.systemoceniania.repository.StudentRepository;
import pl.studenci.systemoceniania.repository.UzytkownikRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UzytkownikRepository uzytkownikRepository;
    private final RolaRepository rolaRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UzytkownikRepository uzytkownikRepository,
                           RolaRepository rolaRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.uzytkownikRepository = uzytkownikRepository;
        this.rolaRepository = rolaRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Rola rolaAdmin    = znajdzLubUtworzRole(Rola.NazwaRoli.ADMIN);
        Rola rolaPracownik = znajdzLubUtworzRole(Rola.NazwaRoli.PRACOWNIK);
        Rola rolaStudent  = znajdzLubUtworzRole(Rola.NazwaRoli.STUDENT);

        dodajUzytkownika("admin",      "admin123",   Set.of(rolaAdmin));
        dodajUzytkownika("nauczyciel", "nauk123",    Set.of(rolaPracownik));

        Student przykladowyStudent = znajdzLubUtworzStudenta("s12345");
        dodajLubAktualizujUzytkownikaStudenta("student", "student123", Set.of(rolaStudent), przykladowyStudent);
    }

    private Student znajdzLubUtworzStudenta(String nrIndeksu) {
        return studentRepository.findByNrIndeksu(nrIndeksu)
                .orElseGet(() -> {
                    Student nowy = new Student();
                    nowy.setNrIndeksu(nrIndeksu);
                    nowy.setImie("Jan");
                    nowy.setNazwisko("Kowalski");
                    nowy.setEmail("jan.kowalski@student.uczelnia.pl");
                    nowy.setDataUrodzenia(LocalDate.of(2000, 1, 1));
                    nowy.setCzyAktywny(true);
                    Student saved = studentRepository.save(nowy);
                    log.info("Utworzono nowego studenta: {} {} (indeks: {})",
                            saved.getImie(), saved.getNazwisko(), saved.getNrIndeksu());
                    return saved;
                });
    }

    private void dodajLubAktualizujUzytkownikaStudenta(String username, String password,
                                                       Set<Rola> role, Student student) {
        Uzytkownik u = uzytkownikRepository.findByUsername(username).orElse(null);
        if (u == null) {
            u = new Uzytkownik();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(password));
            u.setEmail(username + "@uczelnia.pl");
            u.setCzyAktywny(true);
            u.setRole(new HashSet<>(role));
            u.setStudent(student);
            uzytkownikRepository.save(u);
            log.info("Utworzono nowego użytkownika: {} powiązanego ze studentem o indeksie {}",
                    username, student.getNrIndeksu());
        } else if (u.getStudent() == null) {
            u.setStudent(student);
            uzytkownikRepository.save(u);
            log.info("Zaktualizowano użytkownika {} – dodano powiązanie ze studentem o indeksie {}",
                    username, student.getNrIndeksu());
        } else {
            log.debug("Użytkownik {} już istnieje i ma przypisanego studenta (indeks: {})",
                    username, u.getStudent().getNrIndeksu());
        }
    }

    private Rola znajdzLubUtworzRole(Rola.NazwaRoli nazwaEnum) {
        return rolaRepository.findByNazwaRoli(nazwaEnum)
                .orElseGet(() -> {
                    Rola rola = new Rola();
                    rola.setNazwaRoli(nazwaEnum);
                    return rolaRepository.save(rola);
                });
    }

    private void dodajUzytkownika(String username, String rawPassword, Set<Rola> role) {
        if (uzytkownikRepository.findByUsername(username).isEmpty()) {
            Uzytkownik uzytkownik = new Uzytkownik();
            uzytkownik.setUsername(username);
            uzytkownik.setPassword(passwordEncoder.encode(rawPassword));
            uzytkownik.setEmail(username + "@test.pl");
            uzytkownik.setCzyAktywny(true);
            uzytkownik.setRole(new HashSet<>(role));
            uzytkownikRepository.save(uzytkownik);
            log.info("✅ Dodano użytkownika: {}", username);
        } else {
            log.debug("⚠️ Użytkownik {} już istnieje.", username);
        }
    }
}