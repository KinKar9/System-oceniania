package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("/pracownik")
@PreAuthorize("hasRole('PRACOWNIK')")
public class PracownikDashboardController {

    private static final Logger log = LoggerFactory.getLogger(PracownikDashboardController.class);

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }

        // 🔥 SPRAWDZENIE CZY UŻYTKOWNIK MA ROLĘ ADMIN
        Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        boolean isAdmin = roles.contains("ROLE_ADMIN");

        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", isAdmin);  // ← DODANA FLAGA

        log.info("Pracownik {} zalogował się do panelu, isAdmin={}", auth.getName(), isAdmin);

        return "pracownicy/dashboard";
    }
}