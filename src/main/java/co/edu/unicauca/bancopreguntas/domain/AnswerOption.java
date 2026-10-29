package co.edu.unicauca.bancopreguntas.domain;

/** Una opción de respuesta de una pregunta de selección múltiple. Objeto puro, sin anotaciones JPA. */
public class AnswerOption {
    private Long id;
    private String text;
    private boolean correct;

    public AnswerOption() {}
    public AnswerOption(Long id, String text, boolean correct) {
        this.id = id; this.text = text; this.correct = correct;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getText() { return text; }
    public boolean isCorrect() { return correct; }
}
