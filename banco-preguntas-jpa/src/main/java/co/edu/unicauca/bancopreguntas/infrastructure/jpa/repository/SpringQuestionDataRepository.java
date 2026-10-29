package co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository;

import co.edu.unicauca.bancopreguntas.domain.QuestionStatus;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.QuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/** Repositorio Spring Data sobre la tabla base questions (consulta en todos los subtipos). */
public interface SpringQuestionDataRepository extends JpaRepository<QuestionRecord, Long> {
    List<QuestionRecord> findByAuthorId(Long authorId);
    List<QuestionRecord> findByStatus(QuestionStatus status);
    List<QuestionRecord> findByIdIn(List<Long> ids);

    @Query("select q from QuestionRecord q join q.competencies c where c.id = :competencyId and q.status = :status")
    List<QuestionRecord> findByCompetencyIdAndStatus(@Param("competencyId") Long competencyId, @Param("status") QuestionStatus status);
}
