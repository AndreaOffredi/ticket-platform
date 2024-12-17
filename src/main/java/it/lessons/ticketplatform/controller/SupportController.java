package it.lessons.ticketplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.lessons.ticketplatform.model.Ticket;
import it.lessons.ticketplatform.model.Note;
import it.lessons.ticketplatform.model.User;
import it.lessons.ticketplatform.model.User.Role;
import it.lessons.ticketplatform.model.Ticket.Status;
import it.lessons.ticketplatform.repository.NoteRepository;
import it.lessons.ticketplatform.repository.TicketRepository;
import it.lessons.ticketplatform.repository.UserRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/support")
public class SupportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private NoteRepository noteRepository;

    // Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "admin-dashboard";
    }

    @GetMapping("/admin/tickets/search")
    public String searchTickets(@RequestParam String query, Model model) {
        List<Ticket> tickets = ticketRepository.findByTitleContainingIgnoreCase(query);
        model.addAttribute("tickets", tickets);
        return "admin-dashboard";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @GetMapping("/tickets/{id}")
    public String viewTicketDetails(@PathVariable Long id, Model model, Authentication authentication) {
        System.out.println("ID del Ticket caricato: " + id);
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            System.out.println("Ticket trovato: ID = " + ticket.getId() + ", Stato = " + ticket.getStatus());
            model.addAttribute("ticket", ticket);
            model.addAttribute("notes", noteRepository.findByTicketId(id));

            // Aggiungi l'utente corrente al modello
            User currentUser = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);

            return "ticket-details"; // Template condiviso
        }

        // Se il ticket non esiste, reindirizza alla dashboard
        return "redirect:/support/admin/dashboard";
    }


    @GetMapping("/admin/tickets/new")
    public String createTicketForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("operators", userRepository.findByRole(Role.OPERATOR));
        return "create-ticket";
    }

    @PostMapping("/admin/tickets")
    public String createTicket(@ModelAttribute Ticket ticket) {
        ticketRepository.save(ticket);
        return "redirect:/support/admin/dashboard";
    }

    @GetMapping("/admin/tickets/{id}/edit")
    public String editTicketForm(@PathVariable Long id, Model model) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            model.addAttribute("ticket", ticket.get());
            model.addAttribute("operators", userRepository.findByRole(Role.OPERATOR));
            return "edit-ticket";
        }
        return "redirect:/support/admin/dashboard";
    }

    @PostMapping("/admin/tickets/{id}")
    public String updateTicket(@PathVariable Long id, @ModelAttribute Ticket updatedTicket) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            Ticket existingTicket = ticket.get();
            existingTicket.setTitle(updatedTicket.getTitle());
            existingTicket.setDescription(updatedTicket.getDescription());
            existingTicket.setStatus(updatedTicket.getStatus());
            existingTicket.setAssignedOperator(updatedTicket.getAssignedOperator());
            ticketRepository.save(existingTicket);
        }
        return "redirect:/support/admin/dashboard";
    }

    @PostMapping("/admin/tickets/{id}/delete")
    public String deleteTicket(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return "redirect:/support/admin/dashboard";
    }

    // Operator Dashboard
    @GetMapping("/operator/dashboard")
    public String operatorDashboard(Model model, Authentication authentication) {
        // Ottieni l'utente autenticato
        User currentUser = (User) authentication.getPrincipal();
        Long operatorId = currentUser.getId(); // Usa l'ID dell'utente autenticato

        // Recupera i ticket assegnati all'operatore corrente
        List<Ticket> tickets = ticketRepository.findByAssignedOperatorId(operatorId);
        model.addAttribute("tickets", tickets);
        model.addAttribute("operator", currentUser);
        return "operator-dashboard";
    }


    @PreAuthorize("hasRole('OPERATOR')")
    @GetMapping("/operator/profile")
    public String viewOperatorProfile(Authentication authentication, Model model) {
        // Ottieni l'utente attualmente autenticato
        User currentUser = (User) authentication.getPrincipal();

        // Recupera i dati aggiornati dal database
        User operator = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Operatore non trovato"));

        // Aggiungi l'operatore al modello
        model.addAttribute("operator", operator);

        return "operator-profile";
    }

    @PreAuthorize("hasRole('OPERATOR')")
    @PostMapping("/operator/profile/update")
    public String updateOperatorProfile(@ModelAttribute("operator") User updatedOperator, Model model) {
        // Recupera l'utente dal database
        User existingOperator = userRepository.findById(updatedOperator.getId())
                .orElseThrow(() -> new RuntimeException("Operatore non trovato"));

        // Aggiorna solo i campi modificati
        if (updatedOperator.getNome() != null && !updatedOperator.getNome().isBlank()) {
            existingOperator.setNome(updatedOperator.getNome());
        }
        if (updatedOperator.getCognome() != null && !updatedOperator.getCognome().isBlank()) {
            existingOperator.setCognome(updatedOperator.getCognome());
        }
        if (updatedOperator.getEmail() != null && !updatedOperator.getEmail().isBlank()) {
            existingOperator.setEmail(updatedOperator.getEmail());
        }
        if (updatedOperator.getPassword() != null && !updatedOperator.getPassword().isBlank()) {
            existingOperator.setPassword(updatedOperator.getPassword());
        }
        existingOperator.setIsAvailable(updatedOperator.getIsAvailable());

        // Salva l'utente aggiornato
        userRepository.save(existingOperator);

        // Aggiungi i dati aggiornati e un messaggio al modello
        model.addAttribute("operator", existingOperator);
        model.addAttribute("successMessage", "Dati aggiornati correttamente!");

        // Restituisci la pagina del profilo
        return "operator-profile";
    }

    // Ticket Details (Condiviso tra amministratore e operatore)
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PostMapping("/tickets/{id}/status")
    public String updateTicketStatus(@PathVariable Long id, @RequestParam("status") String status) {
        // Recupera il ticket dal database
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isPresent()) {
            Ticket ticket = ticketOptional.get();
            try {
                // Aggiorna lo stato del ticket
                ticket.setStatus(Ticket.Status.valueOf(status));
                ticketRepository.save(ticket);
                System.out.println("Stato del ticket aggiornato a: " + status);
            } catch (IllegalArgumentException e) {
                System.out.println("Stato non valido: " + status);
            }
        } else {
            System.out.println("Ticket non trovato con ID: " + id);
        }
    
        // Reindirizza alla stessa pagina dei dettagli
        return "redirect:/support/tickets/" + id;
    }
    

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PostMapping("/tickets/{id}/notes")
    public String addNoteToTicket(@PathVariable Long id, @RequestParam String content) {
        System.out.println(">>> Metodo POST chiamato correttamente.");
        System.out.println(">>> ID del ticket: " + id);
        System.out.println(">>> Contenuto della nota: " + content);

        // Recupero del ticket
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket non trovato"));

        // Recupero dell'utente corrente
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        System.out.println("Utente corrente: " + currentUser.getId() + " - " + currentUser.getEmail());

        // Creazione e salvataggio della nota
        Note note = new Note();
        note.setContent(content);
        note.setAuthor(currentUser);
        note.setTicket(ticket);

        noteRepository.save(note);
        System.out.println("Nota salvata con successo!");

        // Redirect alla pagina dei dettagli del ticket
        return "redirect:/support/tickets/" + id;
    }
}