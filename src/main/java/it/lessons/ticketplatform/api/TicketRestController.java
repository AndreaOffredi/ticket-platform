package it.lessons.ticketplatform.api;

import it.lessons.ticketplatform.model.Ticket;
import it.lessons.ticketplatform.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {

    @Autowired
    private TicketRepository ticketRepository;

    // 1. Visualizzare l’elenco dei ticket
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // 2. Filtrare l’elenco dei ticket per categoria (title)
    @GetMapping("/category")
    public List<Ticket> getTicketsByCategory(@RequestParam("title") String title) {
        return ticketRepository.findByTitleContainingIgnoreCase(title);
    }

    // 3. Filtrare l’elenco dei ticket per stato
    @GetMapping("/status")
    public List<Ticket> getTicketsByStatus(@RequestParam("status") Ticket.Status status) {
        return ticketRepository.findByStatus(status);
    }
}
