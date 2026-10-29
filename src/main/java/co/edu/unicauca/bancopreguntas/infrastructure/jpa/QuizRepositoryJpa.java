package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.Quiz;
import co.edu.unicauca.bancopreguntas.domain.repository.QuizRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.QuestionRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.QuizRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringQuestionDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringQuizDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringUserDataRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA del contrato QuizRepository. Resuelve el docente y las preguntas (ya PUBLISHED,
 * verificado en el dominio antes de llegar aquí) por identificador y arma la relación N:M ordenada.
 */
@Repository
public class QuizRepositoryJpa implements QuizRepository {
    private final SpringQuizDataRepository quizzes;
    private final SpringQuestionDataRepository questions;
    private final SpringUserDataRepository users;

    public QuizRepositoryJpa(SpringQuizDataRepository quizzes, SpringQuestionDataRepository questions, SpringUserDataRepository users) {
        this.quizzes = quizzes; this.questions = questions; this.users = users;
    }

    @Override
    @Transactional
    public Quiz save(Quiz quiz) {
        UserRecord docente = users.findById(quiz.getCreatedBy())
            .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado: " + quiz.getCreatedBy()));
        List<QuestionRecord> preguntas = questions.findByIdIn(quiz.getQuestionIds());
        QuizRecord record = new QuizRecord(quiz.getTitle(), docente, preguntas);
        QuizRecord saved = quizzes.save(record);
        return toDomain(saved);
    }

    @Override
    public Optional<Quiz> findById(Long id) { return quizzes.findById(id).map(this::toDomain); }

    @Override
    public List<Quiz> findAll() { return quizzes.findAll().stream().map(this::toDomain).toList(); }

    private Quiz toDomain(QuizRecord record) {
        List<Long> questionIds = record.getQuestions().stream().map(QuestionRecord::getId).toList();
        return Quiz.reconstruct(record.getId(), record.getTitle(), record.getCreatedBy().getId(), questionIds);
    }
}
