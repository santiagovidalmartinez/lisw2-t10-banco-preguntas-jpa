package co.edu.unicauca.bancopreguntas.domain.repository;

import co.edu.unicauca.bancopreguntas.domain.Question;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de preguntas, expresado sin JPA ni Spring Data. */
public interface QuestionRepository {
    Question save(Question question);
    Optional<Question> findById(Long id);
    List<Question> findAll();
    List<Question> findByAuthor(Long authorId);
    List<Question> findByStatus(co.edu.unicauca.bancopreguntas.domain.QuestionStatus status);
    List<Question> findPublishedByCompetency(Long competencyId);
    List<Question> findByIds(List<Long> ids);
    void deleteById(Long id);
}
