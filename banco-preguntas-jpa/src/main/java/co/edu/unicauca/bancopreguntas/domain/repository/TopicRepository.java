package co.edu.unicauca.bancopreguntas.domain.repository;

import co.edu.unicauca.bancopreguntas.domain.Topic;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de temas. */
public interface TopicRepository {
    Optional<Topic> findById(Long id);
    List<Topic> findAll();
}
