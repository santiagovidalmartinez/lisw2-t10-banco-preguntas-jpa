package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import co.edu.unicauca.bancopreguntas.domain.CognitiveLevel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Metadatos de una pregunta. Relación 1:1 con QuestionRecord (propietario: questions.metadata_id). */
@Entity
@Table(name = "question_metadata")
public class QuestionMetadataRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "cognitive_level", length = 20)
    private CognitiveLevel cognitiveLevel;

    @Column(name = "estimated_time_minutes")
    private int estimatedTimeMinutes;

    @Column(length = 1000)
    private String notes;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    protected QuestionMetadataRecord() {}

    public QuestionMetadataRecord(CognitiveLevel cognitiveLevel, int estimatedTimeMinutes, String notes, LocalDateTime lastReviewedAt) {
        this.cognitiveLevel = cognitiveLevel; this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.notes = notes; this.lastReviewedAt = lastReviewedAt;
    }

    public Long getId() { return id; }
    public CognitiveLevel getCognitiveLevel() { return cognitiveLevel; }
    public int getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public String getNotes() { return notes; }
    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
}
