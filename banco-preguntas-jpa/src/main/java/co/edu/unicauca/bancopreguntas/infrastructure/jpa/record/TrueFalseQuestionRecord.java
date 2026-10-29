package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import co.edu.unicauca.bancopreguntas.domain.QuestionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Subtipo JPA para preguntas de verdadero/falso. Tabla separada (JOINED), con su única columna extra. */
@Entity
@Table(name = "true_false_questions")
public class TrueFalseQuestionRecord extends QuestionRecord {

    @Column(name = "expected_answer", nullable = false)
    private boolean expectedAnswer;

    protected TrueFalseQuestionRecord() {}

    public TrueFalseQuestionRecord(String statement, QuestionStatus status, UserRecord author, boolean expectedAnswer) {
        super(statement, status, author);
        this.expectedAnswer = expectedAnswer;
    }

    public boolean isExpectedAnswer() { return expectedAnswer; }
    public void setExpectedAnswer(boolean expectedAnswer) { this.expectedAnswer = expectedAnswer; }
}
