package co.edu.unicauca.bancopreguntas.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cuestionario: agrupa preguntas PUBLISHED, en un orden determinado, sin repetir.
 * Solo se crea mediante {@link #create}, que valida las reglas del caso de uso 5.
 */
public class Quiz {
    private Long id;
    private String title;
    private Long createdBy;
    private final List<Long> questionIds = new ArrayList<>();

    private Quiz() {}

    /** Crea un cuestionario validando que todas las preguntas estén PUBLISHED y no se repitan. */
    public static Quiz create(Long id, String title, Long createdBy, List<Question> questions) {
        if (title == null || title.isBlank())
            throw new BusinessRuleViolationException("El cuestionario requiere un título.");
        if (questions == null || questions.isEmpty())
            throw new BusinessRuleViolationException("Un cuestionario requiere al menos una pregunta.");
        Set<Long> vistas = new HashSet<>();
        for (Question pregunta : questions) {
            if (pregunta.getStatus() != QuestionStatus.PUBLISHED)
                throw new BusinessRuleViolationException(
                    "Solo preguntas PUBLISHED pueden incluirse en un cuestionario (pregunta " + pregunta.getId() + " está en " + pregunta.getStatus() + ").");
            if (!vistas.add(pregunta.getId()))
                throw new BusinessRuleViolationException("La pregunta " + pregunta.getId() + " está repetida en el cuestionario.");
        }
        Quiz quiz = new Quiz();
        quiz.id = id; quiz.title = title; quiz.createdBy = createdBy;
        for (Question pregunta : questions) quiz.questionIds.add(pregunta.getId());
        return quiz;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public Long getCreatedBy() { return createdBy; }
    public List<Long> getQuestionIds() { return List.copyOf(questionIds); }

    /**
     * Reconstruye un cuestionario ya persistido a partir de sus identificadores. Lo usa únicamente el
     * mapeador de infraestructura al leer desde la base de datos: no vuelve a validar las reglas de
     * {@link #create} porque, si está en la base de datos, ya las cumplió cuando se creó.
     */
    public static Quiz reconstruct(Long id, String title, Long createdBy, List<Long> questionIds) {
        Quiz quiz = new Quiz();
        quiz.id = id; quiz.title = title; quiz.createdBy = createdBy;
        quiz.questionIds.addAll(questionIds);
        return quiz;
    }
}
