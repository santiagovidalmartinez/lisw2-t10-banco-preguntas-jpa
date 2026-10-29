package co.edu.unicauca.bancopreguntas.domain;

/** Tema académico al que pertenece una pregunta (relación N:1 con Question). */
public class Topic {
    private Long id;
    private String name;

    public Topic() {}
    public Topic(Long id, String name) { this.id = id; this.name = name; }
    public Long getId() { return id; }
    public String getName() { return name; }
}
