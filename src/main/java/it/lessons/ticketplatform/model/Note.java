package it.lessons.ticketplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content; // Contenuto della nota

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket; // Relazione con il ticket associato

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false) // Chiave esterna verso User
    private User author; // Autore della nota

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Data e ora di creazione

    // Costruttore vuoto richiesto da JPA
    public Note() {
        this.createdAt = LocalDateTime.now(); // Imposta la data di creazione automaticamente
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
