package pl.studenci.systemoceniania.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String home() {
        log.debug("Przekierowanie z '/' na '/pracownicy'");
        return "redirect:/pracownicy";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Nieprawidłowa nazwa użytkownika lub hasło");
            log.warn("Nieudana próba logowania");
        }
        if (logout != null) {
            model.addAttribute("message", "Wylogowano pomyślnie");
            log.debug("Użytkownik wylogowany");
        }
        return "login";
    }
}