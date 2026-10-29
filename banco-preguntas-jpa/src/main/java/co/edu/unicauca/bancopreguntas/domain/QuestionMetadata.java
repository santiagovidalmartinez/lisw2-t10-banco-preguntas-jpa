package co.edu.unicauca.bancopreguntas.domain;

import java.time.LocalDateTime;

/** Metadatos de una pregunta (relación 1:1, opcional hasta que se aprueba o rechaza). */
public class QuestionMetadata {
    private Long id;
    private CognitiveLevel cognitiveLevel;
    private int estimatedTimeMinutes;
    private String notes;
    private LocalDateTime lastReviewedAt;

    public QuestionMetadata() {}
    public QuestionMetadata(CognitiveLevel cognitiveLevel, int estimatedTimeMinutes, String notes, LocalDateTime lastReviewedAt) {
        this.cognitiveLevel = cognitiveLevel; this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.notes = notes; this.lastReviewedAt = lastReviewedAt;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CognitiveLevel getCognitiveLevel() { return cognitiveLevel; }
    public int getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public String getNotes() { return notes; }
    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
}
