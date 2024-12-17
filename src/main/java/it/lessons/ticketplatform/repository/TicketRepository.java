package it.lessons.ticketplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.lessons.ticketplatform.model.Ticket;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Query per cercare ticket in base al titolo ignorando il case
    List<Ticket> findByTitleContainingIgnoreCase(String title);

    // Query per cercare ticket assegnati a un operatore specifico
    List<Ticket> findByAssignedOperatorId(Long operatorId);
}
