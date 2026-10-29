package co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository;

import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringUserDataRepository extends JpaRepository<UserRecord, Long> {
    Optional<UserRecord> findByEmail(String email);
}
