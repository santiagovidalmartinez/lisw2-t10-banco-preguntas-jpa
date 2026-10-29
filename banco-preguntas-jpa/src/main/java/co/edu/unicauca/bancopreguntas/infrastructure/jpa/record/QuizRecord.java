package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuestionario. La relación con las preguntas es N:M (una pregunta puede estar en varios cuestionarios)
 * pero necesita conservar el orden, por eso se usa @OrderColumn en lugar de un Set.
 */
@Entity
@Table(name = "quizzes")
public class QuizRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private UserRecord createdBy;

    @ManyToMany
    @JoinTable(name = "quiz_questions",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "question_order")
    private List<QuestionRecord> questions = new ArrayList<>();

    protected QuizRecord() {}

    public QuizRecord(String title, UserRecord createdBy, List<QuestionRecord> questions) {
        this.title = title; this.createdBy = createdBy;
        this.questions = new ArrayList<>(questions);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public UserRecord getCreatedBy() { return createdBy; }
    public List<QuestionRecord> getQuestions() { return questions; }
}
