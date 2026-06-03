package pl.studenci.systemoceniania.service;

// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.studenci.systemoceniania.entity.Uzytkownik;
import pl.studenci.systemoceniania.repository.UzytkownikRepository;
// import java.util.stream.Collectors;

@Service
// public class UzytkownikService implements UserDetailsService {
public class UzytkownikService {
    private final UzytkownikRepository userRepository;

    public UzytkownikService(UzytkownikRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Uzytkownik uzytkownik = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Brak użytkownika: " + username));
        return User.builder()
                .username(uzytkownik.getUsername())
                .password(uzytkownik.getPassword())
                .authorities(uzytkownik.getRole().stream()
                        .map(rola -> new SimpleGrantedAuthority("ROLE_" + rola.getNazwaRoli()))
                        .collect(Collectors.toList()))
                .build();
    }
    */
}