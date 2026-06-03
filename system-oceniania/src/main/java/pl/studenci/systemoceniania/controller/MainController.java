package pl.studenci.systemoceniania.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "redirect:/pracownicy";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}