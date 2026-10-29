package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import co.edu.unicauca.bancopreguntas.domain.QuestionStatus;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA base de la jerarquía de preguntas. Estrategia JOINED: esta tabla (questions) guarda las
 * columnas comunes (enunciado, estado, autor, tema, metadatos, competencias) y cada subtipo concreto
 * tiene su propia tabla con las columnas específicas, unida por la misma clave primaria (id).
 */
@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class QuestionRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String statement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionStatus status = QuestionStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserRecord author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private TopicRecord topic;

    @ManyToMany
    @JoinTable(name = "question_competencies",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "competency_id"))
    private Set<CompetencyRecord> competencies = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "metadata_id", unique = true)
    private QuestionMetadataRecord metadata;

    protected QuestionRecord() {}

    protected QuestionRecord(String statement, QuestionStatus status, UserRecord author) {
        this.statement = statement; this.status = status; this.author = author;
    }

    public Long getId() { return id; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }
    public QuestionStatus getStatus() { return status; }
    public void setStatus(QuestionStatus status) { this.status = status; }
    public UserRecord getAuthor() { return author; }
    public TopicRecord getTopic() { return topic; }
    public void setTopic(TopicRecord topic) { this.topic = topic; }
    public Set<CompetencyRecord> getCompetencies() { return competencies; }
    public void setCompetencies(Set<CompetencyRecord> competencies) { this.competencies = competencies; }
    public QuestionMetadataRecord getMetadata() { return metadata; }
    public void setMetadata(QuestionMetadataRecord metadata) { this.metadata = metadata; }
}
