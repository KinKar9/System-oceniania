package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Zabezpieczenie przed null – unikamy NPE
        if (authentication == null) {
            log.warn("Próba dostępu do dashboardu bez uwierzytelnienia");
            return "redirect:/login";
        }

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        boolean isAdmin = roles.contains(ADMIN_ROLE);
        model.addAttribute("isAdmin", isAdmin);

        // Dodatkowa informacja o roli pracownika (może przydać się w widoku)
        model.addAttribute("isPracownik", roles.contains("ROLE_PRACOWNIK"));

        log.debug("Użytkownik {} zalogowany do dashboardu, isAdmin={}", authentication.getName(), isAdmin);
        return "dashboard";
    }
}