package co.edu.unicauca.bancopreguntas.domain;

/** Estados del ciclo de vida de una pregunta. */
public enum QuestionStatus {
    DRAFT, PENDING_REVIEW, IN_REVIEW, APPROVED, PUBLISHED, ARCHIVED
}
