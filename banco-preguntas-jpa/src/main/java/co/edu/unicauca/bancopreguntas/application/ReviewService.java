package co.edu.unicauca.bancopreguntas.application;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.domain.repository.QuestionRepository;
import co.edu.unicauca.bancopreguntas.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Flujo de revisión: tomar, aprobar, rechazar, publicar y archivar una pregunta. */
@Service
@Transactional
public class ReviewService {
    private final QuestionRepository questions;
    private final UserRepository users;

    public ReviewService(QuestionRepository questions, UserRepository users) {
        this.questions = questions; this.users = users;
    }

    public Question startReview(Long questionId, Long reviewerId) {
        requireReviewer(reviewerId);
        Question pregunta = find(questionId);
        pregunta.startReview();
        return questions.save(pregunta);
    }

    public Question approve(Long questionId, String notes) {
        Question pregunta = find(questionId);
        pregunta.approve(notes);
        return questions.save(pregunta);
    }

    public Question reject(Long questionId, String notes) {
        Question pregunta = find(questionId);
        pregunta.reject(notes);
        return questions.save(pregunta);
    }

    public Question publish(Long questionId) {
        Question pregunta = find(questionId);
        pregunta.publish();
        return questions.save(pregunta);
    }

    public Question archive(Long questionId) {
        Question pregunta = find(questionId);
        pregunta.archive();
        return questions.save(pregunta);
    }

    private Question find(Long id) {
        return questions.findById(id).orElseThrow(() -> new NotFoundException("Pregunta no encontrada: " + id));
    }

    private void requireReviewer(Long reviewerId) {
        User revisor = users.findById(reviewerId).orElseThrow(() -> new NotFoundException("Revisor no encontrado: " + reviewerId));
        if (!revisor.hasRole(Role.REVIEWER))
            throw new BusinessRuleViolationException("El usuario " + reviewerId + " no tiene el rol REVIEWER.");
    }
}
