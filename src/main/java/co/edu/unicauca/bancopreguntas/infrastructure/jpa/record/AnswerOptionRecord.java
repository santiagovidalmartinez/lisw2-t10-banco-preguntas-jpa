package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import jakarta.persistence.*;

/** Opción de respuesta. Lado N de la relación 1:N con MultipleChoiceQuestionRecord (composición). */
@Entity
@Table(name = "answer_options")
public class AnswerOptionRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private boolean correct;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private MultipleChoiceQuestionRecord question;

    protected AnswerOptionRecord() {}

    public AnswerOptionRecord(String text, boolean correct) {
        this.text = text; this.correct = correct;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public boolean isCorrect() { return correct; }
    public MultipleChoiceQuestionRecord getQuestion() { return question; }
    public void setQuestion(MultipleChoiceQuestionRecord question) { this.question = question; }
}
