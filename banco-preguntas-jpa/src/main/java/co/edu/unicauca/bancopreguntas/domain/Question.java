package co.edu.unicauca.bancopreguntas.domain;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Clase base del dominio para una pregunta del Banco de Preguntas Saber PRO.
 * No tiene ninguna anotación de persistencia: la jerarquía de dominio (Question, MultipleChoiceQuestion,
 * TrueFalseQuestion) es independiente de la jerarquía JPA (QuestionRecord y subclases), que vive en
 * infrastructure.jpa.record.
 */
public abstract class Question {
    protected Long id;
    protected String statement;
    protected Long authorId;
    protected Long topicId;
    protected final Set<Long> competencyIds = new LinkedHashSet<>();
    protected QuestionStatus status = QuestionStatus.DRAFT;
    protected QuestionMetadata metadata;

    protected Question() {}

    protected Question(Long id, String statement, Long authorId) {
        this.id = id; this.statement = statement; this.authorId = authorId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatement() { return statement; }
    public Long getAuthorId() { return authorId; }
    public Long getTopicId() { return topicId; }
    public Set<Long> getCompetencyIds() { return Set.copyOf(competencyIds); }
    public QuestionStatus getStatus() { return status; }
    public QuestionMetadata getMetadata() { return metadata; }
    public void setMetadata(QuestionMetadata metadata) { this.metadata = metadata; }

    /** Solo los borradores pueden reclasificarse (Parte II, regla de negocio). */
    public void classify(Long topicId, Set<Long> competencyIds) {
        requireDraft("reclasificar la pregunta");
        if (topicId == null) throw new BusinessRuleViolationException("La pregunta debe tener un tema.");
        if (competencyIds == null || competencyIds.isEmpty())
            throw new BusinessRuleViolationException("La pregunta debe tener al menos una competencia.");
        this.topicId = topicId;
        this.competencyIds.clear();
        this.competencyIds.addAll(competencyIds);
    }

    /** DRAFT -> PENDING_REVIEW. Exige tema, al menos una competencia y las reglas propias del tipo. */
    public void submitForReview() {
        if (status != QuestionStatus.DRAFT)
            throw new BusinessRuleViolationException("Solo una pregunta en borrador puede enviarse a revisión (estado actual: " + status + ").");
        if (topicId == null)
            throw new BusinessRuleViolationException("No se puede enviar a revisión una pregunta sin tema.");
        if (competencyIds.isEmpty())
            throw new BusinessRuleViolationException("No se puede enviar a revisión una pregunta sin competencias.");
        validateTypeSpecificRules();
        status = QuestionStatus.PENDING_REVIEW;
    }

    /** PENDING_REVIEW -> IN_REVIEW, cuando un revisor toma la pregunta. */
    public void startReview() { transition(QuestionStatus.PENDING_REVIEW, QuestionStatus.IN_REVIEW); }

    /** IN_REVIEW -> APPROVED. */
    public void approve(String notes) {
        transition(QuestionStatus.IN_REVIEW, QuestionStatus.APPROVED);
        touchMetadata(notes);
    }

    /** IN_REVIEW -> DRAFT (rechazo: la pregunta vuelve al autor). */
    public void reject(String notes) {
        transition(QuestionStatus.IN_REVIEW, QuestionStatus.DRAFT);
        touchMetadata(notes);
    }

    /** APPROVED -> PUBLISHED. Solo desde aquí una pregunta queda disponible para Quiz.create(...). */
    public void publish() { transition(QuestionStatus.APPROVED, QuestionStatus.PUBLISHED); }

    /** PUBLISHED -> ARCHIVED. */
    public void archive() { transition(QuestionStatus.PUBLISHED, QuestionStatus.ARCHIVED); }

    private void transition(QuestionStatus from, QuestionStatus to) {
        if (status != from)
            throw new BusinessRuleViolationException(
                "Transición inválida de " + status + " a " + to + " (se esperaba estar en " + from + ").");
        status = to;
    }

    private void touchMetadata(String notes) {
        CognitiveLevel level = metadata != null ? metadata.getCognitiveLevel() : CognitiveLevel.UNDERSTAND;
        int minutes = metadata != null ? metadata.getEstimatedTimeMinutes() : 2;
        this.metadata = new QuestionMetadata(level, minutes, notes, LocalDateTime.now());
    }

    protected void requireDraft(String accion) {
        if (status != QuestionStatus.DRAFT)
            throw new BusinessRuleViolationException("Solo se puede " + accion + " mientras la pregunta está en DRAFT (estado actual: " + status + ").");
    }

    /** Reglas propias de cada tipo de pregunta, evaluadas al enviar a revisión. */
    protected abstract void validateTypeSpecificRules();

    /**
     * Reconstruye el estado completo de una pregunta ya persistida. Lo usa únicamente el mapeador de
     * infraestructura (QuestionMapper) al leer desde la base de datos: no pasa por las validaciones de
     * transición porque el estado que llega ya es válido (vino de una transición que sí se validó antes
     * de guardarse). No debe usarse para cambiar el estado de una pregunta en memoria durante un caso de uso.
     */
    public void restoreState(QuestionStatus status, Long topicId, Set<Long> competencyIds, QuestionMetadata metadata) {
        this.status = status;
        this.topicId = topicId;
        this.competencyIds.clear();
        if (competencyIds != null) this.competencyIds.addAll(competencyIds);
        this.metadata = metadata;
    }
}
