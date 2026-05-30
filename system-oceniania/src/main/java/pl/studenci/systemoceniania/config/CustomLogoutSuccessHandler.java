package pl.studenci.systemoceniania.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pl.studenci.systemoceniania.entity.HistoriaLogowania;
import pl.studenci.systemoceniania.repository.HistoriaLogowaniaRepository;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final HistoriaLogowaniaRepository historiaRepository;

    public CustomLogoutSuccessHandler(HistoriaLogowaniaRepository historiaRepository) {
        this.historiaRepository = historiaRepository;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        if (authentication != null && authentication.getName() != null) {
            HistoriaLogowania log = new HistoriaLogowania();
            log.setUsername(authentication.getName());
            log.setDataWylogowania(LocalDateTime.now());

            historiaRepository.save(log);
        }

        response.sendRedirect("/login?logout");
    }
}