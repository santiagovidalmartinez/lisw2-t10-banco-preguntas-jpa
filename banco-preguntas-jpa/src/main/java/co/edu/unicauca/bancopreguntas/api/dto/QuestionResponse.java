package co.edu.unicauca.bancopreguntas.api.dto;

import co.edu.unicauca.bancopreguntas.domain.*;
import java.util.List;
import java.util.Set;

/** Representación de una pregunta hacia afuera de la API (no es ni Question ni QuestionRecord). */
public class QuestionResponse {
    public Long id;
    public String statement;
    public String type; // MULTIPLE_CHOICE | TRUE_FALSE
    public String status;
    public Long authorId;
    public Long topicId;
    public Set<Long> competencyIds;
    public List<OptionResponse> options; // solo para MULTIPLE_CHOICE
    public Boolean expectedAnswer; // solo para TRUE_FALSE

    public static QuestionResponse from(Question question) {
        QuestionResponse r = new QuestionResponse();
        r.id = question.getId();
        r.statement = question.getStatement();
        r.status = question.getStatus().name();
        r.authorId = question.getAuthorId();
        r.topicId = question.getTopicId();
        r.competencyIds = question.getCompetencyIds();
        if (question instanceof MultipleChoiceQuestion mc) {
            r.type = "MULTIPLE_CHOICE";
            r.options = mc.getOptions().stream().map(OptionResponse::from).toList();
        } else if (question instanceof TrueFalseQuestion tf) {
            r.type = "TRUE_FALSE";
            r.expectedAnswer = tf.getExpectedAnswer();
        }
        return r;
    }

    public static class OptionResponse {
        public Long id;
        public String text;
        public boolean correct;

        public static OptionResponse from(AnswerOption option) {
            OptionResponse r = new OptionResponse();
            r.id = option.getId(); r.text = option.getText(); r.correct = option.isCorrect();
            return r;
        }
    }
}
