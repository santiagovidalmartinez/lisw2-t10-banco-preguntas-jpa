package co.edu.unicauca.bancopreguntas.domain;

/** Pregunta de verdadero/falso: solo guarda la respuesta esperada. */
public class TrueFalseQuestion extends Question {
    private boolean expectedAnswer;

    public TrueFalseQuestion() {}
    public TrueFalseQuestion(Long id, String statement, Long authorId, boolean expectedAnswer) {
        super(id, statement, authorId);
        this.expectedAnswer = expectedAnswer;
    }

    public boolean getExpectedAnswer() { return expectedAnswer; }

    @Override
    protected void validateTypeSpecificRules() {
        // No tiene reglas estructurales adicionales a las de Question (tema + competencias).
    }
}
