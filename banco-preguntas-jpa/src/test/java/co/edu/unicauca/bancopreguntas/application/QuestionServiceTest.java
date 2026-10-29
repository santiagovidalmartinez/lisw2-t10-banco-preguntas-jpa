package co.edu.unicauca.bancopreguntas.application;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba QuestionService con repositorios en memoria (implementaciones falsas de las interfaces de
 * domain.repository), sin JPA. Así se verifica el caso de uso sin depender de Spring Data ni de una
 * base de datos real, tal como lo hace StudentServiceTest en el demo de referencia.
 */
class QuestionServiceTest {
    private InMemoryQuestionRepository questions;
    private InMemoryUserRepository users;
    private InMemoryTopicRepository topics;
    private InMemoryCompetencyRepository competencies;
    private QuestionService service;

    @BeforeEach
    void setUp() {
        questions = new InMemoryQuestionRepository();
        users = new InMemoryUserRepository();
        topics = new InMemoryTopicRepository();
        competencies = new InMemoryCompetencyRepository();
        service = new QuestionService(questions, users, topics, competencies);

        users.save(new User(1L, "Ana Autora", "ana@unicauca.edu.co", Set.of(Role.AUTHOR)));
        users.save(new User(2L, "Diego Docente", "diego@unicauca.edu.co", Set.of(Role.TEACHER)));
        topics.save(new Topic(10L, "Álgebra"));
        competencies.save(new Competency(20L, "Razonamiento Cuantitativo"));
    }

    @Test
    void creaPreguntaMultipleChoiceValida() {
        List<AnswerOption> opciones = List.of(
            new AnswerOption(null, "4", true), new AnswerOption(null, "3", false),
            new AnswerOption(null, "5", false), new AnswerOption(null, "6", false), new AnswerOption(null, "7", false));
        Question pregunta = service.createMultipleChoice("¿Cuánto es 2+2?", 1L, opciones);
        assertEquals(QuestionStatus.DRAFT, pregunta.getStatus());
        assertNotNull(pregunta.getId());
    }

    @Test
    void crearConAutorSinRolAuthorFalla() {
        assertThrows(BusinessRuleViolationException.class,
            () -> service.createTrueFalse("¿Verdadero?", 2L, true)); // 2L es TEACHER, no AUTHOR
    }

    @Test
    void crearConAutorInexistenteFalla() {
        assertThrows(NotFoundException.class, () -> service.createTrueFalse("¿Pregunta?", 999L, true));
    }

    @Test
    void clasificarYEnviarARevisionFlujoCompleto() {
        Question pregunta = service.createTrueFalse("¿Spring Boot usa Tomcat embebido por defecto?", 1L, true);
        service.classify(pregunta.getId(), 10L, Set.of(20L));
        Question enviada = service.submitForReview(pregunta.getId());
        assertEquals(QuestionStatus.PENDING_REVIEW, enviada.getStatus());
    }

    @Test
    void clasificarConTemaInexistenteFalla() {
        Question pregunta = service.createTrueFalse("¿Pregunta?", 1L, true);
        assertThrows(NotFoundException.class, () -> service.classify(pregunta.getId(), 999L, Set.of(20L)));
    }

    @Test
    void eliminarSoloPermiteBorradores() {
        Question pregunta = service.createTrueFalse("¿Pregunta?", 1L, true);
        service.classify(pregunta.getId(), 10L, Set.of(20L));
        service.submitForReview(pregunta.getId());
        assertThrows(BusinessRuleViolationException.class, () -> service.delete(pregunta.getId()));
    }

    // ---- Repositorios en memoria (fakes), equivalentes a los que usaría cualquier otro proveedor de persistencia ----

    static class InMemoryQuestionRepository implements QuestionRepository {
        private final Map<Long, Question> data = new HashMap<>();
        private long seq = 1;
        public Question save(Question q) {
            if (q.getId() == null) q.setId(seq++);
            data.put(q.getId(), q);
            return q;
        }
        public Optional<Question> findById(Long id) { return Optional.ofNullable(data.get(id)); }
        public List<Question> findAll() { return List.copyOf(data.values()); }
        public List<Question> findByAuthor(Long authorId) { return data.values().stream().filter(q -> q.getAuthorId().equals(authorId)).toList(); }
        public List<Question> findByStatus(QuestionStatus status) { return data.values().stream().filter(q -> q.getStatus() == status).toList(); }
        public List<Question> findPublishedByCompetency(Long competencyId) {
            return data.values().stream().filter(q -> q.getStatus() == QuestionStatus.PUBLISHED && q.getCompetencyIds().contains(competencyId)).toList();
        }
        public List<Question> findByIds(List<Long> ids) { return ids.stream().map(data::get).filter(Objects::nonNull).toList(); }
        public void deleteById(Long id) { data.remove(id); }
    }

    static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, User> data = new HashMap<>();
        public User save(User u) { data.put(u.getId(), u); return u; }
        public Optional<User> findById(Long id) { return Optional.ofNullable(data.get(id)); }
        public Optional<User> findByEmail(String email) { return data.values().stream().filter(u -> u.getEmail().equals(email)).findFirst(); }
    }

    static class InMemoryTopicRepository implements TopicRepository {
        private final Map<Long, Topic> data = new HashMap<>();
        public Topic save(Topic t) { data.put(t.getId(), t); return t; }
        public Optional<Topic> findById(Long id) { return Optional.ofNullable(data.get(id)); }
        public List<Topic> findAll() { return List.copyOf(data.values()); }
    }

    static class InMemoryCompetencyRepository implements CompetencyRepository {
        private final Map<Long, Competency> data = new HashMap<>();
        public Competency save(Competency c) { data.put(c.getId(), c); return c; }
        public Optional<Competency> findById(Long id) { return Optional.ofNullable(data.get(id)); }
        public List<Competency> findAllByIds(List<Long> ids) { return ids.stream().map(data::get).filter(Objects::nonNull).toList(); }
        public List<Competency> findAll() { return List.copyOf(data.values()); }
    }
}
