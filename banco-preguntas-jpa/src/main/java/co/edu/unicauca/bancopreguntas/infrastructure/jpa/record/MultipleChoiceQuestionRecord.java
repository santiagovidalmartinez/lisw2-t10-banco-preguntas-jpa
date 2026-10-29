package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import co.edu.unicauca.bancopreguntas.domain.QuestionStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Subtipo JPA para preguntas de selección múltiple. Tabla separada (JOINED), con sus opciones 1:N. */
@Entity
@Table(name = "multiple_choice_questions")
public class MultipleChoiceQuestionRecord extends QuestionRecord {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "option_order")
    private List<AnswerOptionRecord> options = new ArrayList<>();

    protected MultipleChoiceQuestionRecord() {}

    public MultipleChoiceQuestionRecord(String statement, QuestionStatus status, UserRecord author) {
        super(statement, status, author);
    }

    public List<AnswerOptionRecord> getOptions() { return options; }

    public void addOption(AnswerOptionRecord option) {
        option.setQuestion(this);
        options.add(option);
    }

    public void clearOptions() {
        options.forEach(o -> o.setQuestion(null));
        options.clear();
    }
}
