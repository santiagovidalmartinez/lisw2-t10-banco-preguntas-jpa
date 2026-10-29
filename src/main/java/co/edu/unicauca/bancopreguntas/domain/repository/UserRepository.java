package co.edu.unicauca.bancopreguntas.domain.repository;

import co.edu.unicauca.bancopreguntas.domain.User;
import java.util.Optional;

/** Contrato de persistencia de usuarios. */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}
