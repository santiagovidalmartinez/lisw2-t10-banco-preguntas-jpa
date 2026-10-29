package co.edu.unicauca.bancopreguntas.domain;

import java.util.ArrayList;
import java.util.List;

/** Pregunta de selección múltiple: exactamente 5 opciones, una única correcta, sin textos repetidos. */
public class MultipleChoiceQuestion extends Question {
    private final List<AnswerOption> options = new ArrayList<>();

    public MultipleChoiceQuestion() {}
    public MultipleChoiceQuestion(Long id, String statement, Long authorId) { super(id, statement, authorId); }

    /** Agrega una opción. Solo permitido en DRAFT; máximo 5, sin texto repetido, una sola marcada como correcta. */
    public void addOption(AnswerOption option) {
        requireDraft("agregar una opción de respuesta");
        if (options.size() >= 5)
            throw new BusinessRuleViolationException("Una pregunta de selección múltiple admite máximo 5 opciones.");
        boolean textoRepetido = options.stream().anyMatch(o -> o.getText().equalsIgnoreCase(option.getText()));
        if (textoRepetido)
            throw new BusinessRuleViolationException("No se permiten opciones con el mismo texto.");
        if (option.isCorrect() && options.stream().anyMatch(AnswerOption::isCorrect))
            throw new BusinessRuleViolationException("Ya existe una opción marcada como correcta.");
        options.add(option);
    }

    public List<AnswerOption> getOptions() { return List.copyOf(options); }

    @Override
    protected void validateTypeSpecificRules() {
        if (options.size() != 5)
            throw new BusinessRuleViolationException("Una pregunta de selección múltiple requiere exactamente 5 opciones (tiene " + options.size() + ").");
        long correctas = options.stream().filter(AnswerOption::isCorrect).count();
        if (correctas != 1)
            throw new BusinessRuleViolationException("Debe existir exactamente una opción correcta (hay " + correctas + ").");
    }
}
