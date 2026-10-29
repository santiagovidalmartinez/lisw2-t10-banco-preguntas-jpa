package co.edu.unicauca.bancopreguntas.application;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Prueba ReviewService y QuizService con repositorios en memoria (casos de uso 2 y 5). */
class ReviewAndQuizServiceTest {
    private QuestionServiceTest.InMemoryQuestionRepository questions;
    private QuestionServiceTest.InMemoryUserRepository users;
    private QuestionServiceTest.InMemoryTopicRepository topics;
    private QuestionServiceTest.InMemoryCompetencyRepository competencies;
    private InMemoryQuizRepository quizzes;
    private QuestionService questionService;
    private ReviewService reviewService;
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        questions = new QuestionServiceTest.InMemoryQuestionRepository();
        users = new QuestionServiceTest.InMemoryUserRepository();
        topics = new QuestionServiceTest.InMemoryTopicRepository();
        competencies = new QuestionServiceTest.InMemoryCompetencyRepository();
        quizzes = new InMemoryQuizRepository();
        questionService = new QuestionService(questions, users, topics, competencies);
        reviewService = new ReviewService(questions, users);
        quizService = new QuizService(quizzes, questions, users);

        users.save(new User(1L, "Ana Autora", "ana@unicauca.edu.co", Set.of(Role.AUTHOR)));
        users.save(new User(2L, "Raúl Revisor", "raul@unicauca.edu.co", Set.of(Role.REVIEWER)));
        users.save(new User(3L, "Diego Docente", "diego@unicauca.edu.co", Set.of(Role.TEACHER)));
        topics.save(new Topic(10L, "Álgebra"));
        competencies.save(new Competency(20L, "Razonamiento Cuantitativo"));
    }

    private Question preguntaEnPendingReview() {
        Question p = questionService.createTrueFalse("¿2 es par?", 1L, true);
        questionService.classify(p.getId(), 10L, Set.of(20L));
        return questionService.submitForReview(p.getId());
    }

    @Test
    void flujoDeRevisionCompletoHastaPublicada() {
        Question p = preguntaEnPendingReview();
        reviewService.startReview(p.getId(), 2L);
        Question aprobada = reviewService.approve(p.getId(), "Correcta");
        assertEquals(QuestionStatus.APPROVED, aprobada.getStatus());
        Question publicada = reviewService.publish(p.getId());
        assertEquals(QuestionStatus.PUBLISHED, publicada.getStatus());
    }

    @Test
    void revisorSinRolFalla() {
        Question p = preguntaEnPendingReview();
        assertThrows(BusinessRuleViolationException.class, () -> reviewService.startReview(p.getId(), 1L)); // 1L es AUTHOR
    }

    @Test
    void rechazoDevuelveLaPreguntaADraft() {
        Question p = preguntaEnPendingReview();
        reviewService.startReview(p.getId(), 2L);
        Question rechazada = reviewService.reject(p.getId(), "Ambigua");
        assertEquals(QuestionStatus.DRAFT, rechazada.getStatus());
    }

    @Test
    void crearQuizConPreguntasPublicadas() {
        Question p1 = publicar(preguntaEnPendingReview());
        Question p2 = publicar(preguntaEnPendingReview());
        Quiz quiz = quizService.createQuiz("Parcial 1", 3L, List.of(p1.getId(), p2.getId()));
        assertEquals(2, quiz.getQuestionIds().size());
    }

    @Test
    void crearQuizConPreguntaNoPublicadaFalla() {
        Question draft = questionService.createTrueFalse("¿Sin publicar?", 1L, false);
        assertThrows(BusinessRuleViolationException.class,
            () -> quizService.createQuiz("Parcial 2", 3L, List.of(draft.getId())));
    }

    @Test
    void crearQuizConDocenteSinRolFalla() {
        Question p1 = publicar(preguntaEnPendingReview());
        assertThrows(BusinessRuleViolationException.class,
            () -> quizService.createQuiz("Parcial 3", 1L, List.of(p1.getId()))); // 1L es AUTHOR, no TEACHER
    }

    private Question publicar(Question p) {
        reviewService.startReview(p.getId(), 2L);
        reviewService.approve(p.getId(), "ok");
        return reviewService.publish(p.getId());
    }

    static class InMemoryQuizRepository implements QuizRepository {
        private final Map<Long, Quiz> data = new HashMap<>();
        private long seq = 1;
        public Quiz save(Quiz q) {
            if (q.getId() == null) q.setId(seq++);
            data.put(q.getId(), q);
            return q;
        }
        public Optional<Quiz> findById(Long id) { return Optional.ofNullable(data.get(id)); }
        public List<Quiz> findAll() { return List.copyOf(data.values()); }
    }
}
