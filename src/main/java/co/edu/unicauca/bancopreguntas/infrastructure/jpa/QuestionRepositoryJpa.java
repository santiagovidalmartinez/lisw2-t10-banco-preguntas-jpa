package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.Question;
import co.edu.unicauca.bancopreguntas.domain.QuestionStatus;
import co.edu.unicauca.bancopreguntas.domain.repository.QuestionRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.*;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación concreta del contrato de dominio QuestionRepository. Es la única clase que conoce al
 * mismo tiempo el modelo de dominio (Question) y el modelo JPA (QuestionRecord): resuelve las entidades
 * relacionadas (autor, tema, competencias) por identificador y convierte en ambos sentidos con QuestionMapper.
 */
@Repository
public class QuestionRepositoryJpa implements QuestionRepository {
    private final SpringQuestionDataRepository questions;
    private final SpringUserDataRepository users;
    private final SpringTopicDataRepository topics;
    private final SpringCompetencyDataRepository competencies;

    public QuestionRepositoryJpa(SpringQuestionDataRepository questions, SpringUserDataRepository users,
                                  SpringTopicDataRepository topics, SpringCompetencyDataRepository competencies) {
        this.questions = questions; this.users = users;
        this.topics = topics; this.competencies = competencies;
    }

    @Override
    @Transactional
    public Question save(Question question) {
        QuestionRecord record;
        if (question.getId() == null) {
            UserRecord author = users.findById(question.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado: " + question.getAuthorId()));
            record = QuestionMapper.toNewRecord(question, author);
        } else {
            record = questions.findById(question.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada: " + question.getId()));
            record.setStatus(question.getStatus());
        }
        if (question.getTopicId() != null) {
            TopicRecord topic = topics.findById(question.getTopicId())
                .orElseThrow(() -> new IllegalArgumentException("Tema no encontrado: " + question.getTopicId()));
            record.setTopic(topic);
        }
        if (!question.getCompetencyIds().isEmpty()) {
            Set<CompetencyRecord> competencyRecords = competencies.findByIdIn(List.copyOf(question.getCompetencyIds()))
                .stream().collect(Collectors.toSet());
            record.setCompetencies(competencyRecords);
        }
        if (question.getMetadata() != null) {
            record.setMetadata(QuestionMapper.toRecordMetadata(question.getMetadata()));
        }
        QuestionRecord saved = questions.save(record);
        return QuestionMapper.toDomain(saved);
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questions.findById(id).map(QuestionMapper::toDomain);
    }

    @Override
    public List<Question> findAll() {
        return questions.findAll().stream().map(QuestionMapper::toDomain).toList();
    }

    @Override
    public List<Question> findByAuthor(Long authorId) {
        return questions.findByAuthorId(authorId).stream().map(QuestionMapper::toDomain).toList();
    }

    @Override
    public List<Question> findByStatus(QuestionStatus status) {
        return questions.findByStatus(status).stream().map(QuestionMapper::toDomain).toList();
    }

    @Override
    public List<Question> findPublishedByCompetency(Long competencyId) {
        return questions.findByCompetencyIdAndStatus(competencyId, QuestionStatus.PUBLISHED)
            .stream().map(QuestionMapper::toDomain).toList();
    }

    @Override
    public List<Question> findByIds(List<Long> ids) {
        return questions.findByIdIn(ids).stream().map(QuestionMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        questions.deleteById(id);
    }
}
