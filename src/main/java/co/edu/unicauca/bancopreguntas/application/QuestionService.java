package co.edu.unicauca.bancopreguntas.application;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

/**
 * Casos de uso 1 (crear), 3 (clasificar) y 4 (enviar a revisión).
 * Solo depende de interfaces de domain.repository: no importa JPA ni Spring Data (DIP).
 */
@Service
@Transactional
public class QuestionService {
    private final QuestionRepository questions;
    private final UserRepository users;
    private final TopicRepository topics;
    private final CompetencyRepository competencies;

    public QuestionService(QuestionRepository questions, UserRepository users,
                            TopicRepository topics, CompetencyRepository competencies) {
        this.questions = questions; this.users = users;
        this.topics = topics; this.competencies = competencies;
    }

    /** Crea una pregunta de selección múltiple en DRAFT con sus 5 opciones. */
    public Question createMultipleChoice(String statement, Long authorId, List<AnswerOption> options) {
        requireAuthor(authorId);
        MultipleChoiceQuestion pregunta = new MultipleChoiceQuestion(null, statement, authorId);
        for (AnswerOption opcion : options) pregunta.addOption(opcion);
        return questions.save(pregunta);
    }

    /** Crea una pregunta de verdadero/falso en DRAFT. */
    public Question createTrueFalse(String statement, Long authorId, boolean expectedAnswer) {
        requireAuthor(authorId);
        TrueFalseQuestion pregunta = new TrueFalseQuestion(null, statement, authorId, expectedAnswer);
        return questions.save(pregunta);
    }

    /** Caso de uso 3: clasificar (tema + competencias). Solo preguntas DRAFT. */
    public Question classify(Long questionId, Long topicId, Set<Long> competencyIds) {
        Question pregunta = findQuestion(questionId);
        if (!topics.findById(topicId).isPresent())
            throw new NotFoundException("Tema no encontrado: " + topicId);
        List<co.edu.unicauca.bancopreguntas.domain.Competency> encontradas = competencies.findAllByIds(List.copyOf(competencyIds));
        if (encontradas.size() != competencyIds.size())
            throw new NotFoundException("Una o más competencias no existen: " + competencyIds);
        pregunta.classify(topicId, competencyIds);
        return questions.save(pregunta);
    }

    /** Caso de uso 4: enviar a revisión. El dominio valida las condiciones mínimas; transaccional en la implementación JPA. */
    public Question submitForReview(Long questionId) {
        Question pregunta = findQuestion(questionId);
        pregunta.submitForReview();
        return questions.save(pregunta);
    }

    /** Solo los borradores pueden eliminarse. */
    public void delete(Long questionId) {
        Question pregunta = findQuestion(questionId);
        if (pregunta.getStatus() != QuestionStatus.DRAFT)
            throw new BusinessRuleViolationException("Solo se pueden eliminar preguntas en DRAFT.");
        questions.deleteById(questionId);
    }

    public Question findQuestion(Long id) {
        return questions.findById(id).orElseThrow(() -> new NotFoundException("Pregunta no encontrada: " + id));
    }

    private void requireAuthor(Long authorId) {
        User autor = users.findById(authorId).orElseThrow(() -> new NotFoundException("Autor no encontrado: " + authorId));
        if (!autor.hasRole(Role.AUTHOR))
            throw new BusinessRuleViolationException("El usuario " + authorId + " no tiene el rol AUTHOR.");
    }
}
