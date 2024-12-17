package it.lessons.ticketplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import it.lessons.ticketplatform.model.Note;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    
    // Query per trovare tutte le note associate a un ticket specifico
    List<Note> findByTicketId(Long ticketId);
}
