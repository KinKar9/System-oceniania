package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        // Zabezpieczenie przed null – unikamy NPE
        if (authentication == null) {
            log.warn("Próba dostępu do dashboardu bez uwierzytelnienia");
            return "redirect:/login";
        }

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_STUDENT")) {
            log.debug("Przekierowanie studenta: {}", authentication.getName());
            return "redirect:/student/dashboard";
        } else if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_PRACOWNIK")) {
            log.debug("Przekierowanie pracownika/admina: {}", authentication.getName());
            return "redirect:/pracownik/dashboard";
        }

        log.warn("Użytkownik {} nie ma przypisanej roli", authentication.getName());
        return "redirect:/login";
    }
}