package it.lessons.ticketplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {

    @GetMapping("/error")
    public String handleError() {
        return "error"; // Una pagina `error.html` nel tuo template
    }
}
