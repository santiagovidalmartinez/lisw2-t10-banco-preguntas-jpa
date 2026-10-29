package co.edu.unicauca.bancopreguntas.domain.repository;

import co.edu.unicauca.bancopreguntas.domain.Quiz;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de cuestionarios. */
public interface QuizRepository {
    Quiz save(Quiz quiz);
    Optional<Quiz> findById(Long id);
    List<Quiz> findAll();
}
