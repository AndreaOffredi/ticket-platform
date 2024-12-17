package it.lessons.ticketplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {

    //Ho provato a creare una pagina per gli errori ma non funziona correttamente
    @GetMapping("/error")
    public String handleError() {
        return "error"; // Una pagina `error.html (MANCANTE)
    }
}
