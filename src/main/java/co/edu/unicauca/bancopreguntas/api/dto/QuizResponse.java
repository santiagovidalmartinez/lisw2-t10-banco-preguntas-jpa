package co.edu.unicauca.bancopreguntas.api.dto;

import co.edu.unicauca.bancopreguntas.domain.Quiz;
import java.util.List;

public class QuizResponse {
    public Long id;
    public String title;
    public Long createdBy;
    public List<Long> questionIds;

    public static QuizResponse from(Quiz quiz) {
        QuizResponse r = new QuizResponse();
        r.id = quiz.getId(); r.title = quiz.getTitle(); r.createdBy = quiz.getCreatedBy();
        r.questionIds = quiz.getQuestionIds();
        return r;
    }
}
