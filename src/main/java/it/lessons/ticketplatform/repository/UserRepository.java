package it.lessons.ticketplatform.repository;

import it.lessons.ticketplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Query derivata per trovare un utente tramite email
    User findByEmail(String email);

    // Query per trovare tutti gli utenti con un determinato ruolo
    List<User> findByRole(User.Role role);
}
