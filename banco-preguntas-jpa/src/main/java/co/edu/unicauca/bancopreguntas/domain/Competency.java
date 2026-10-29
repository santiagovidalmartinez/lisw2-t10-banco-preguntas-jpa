package co.edu.unicauca.bancopreguntas.domain;

/** Competencia evaluada por una pregunta (relación N:M con Question). */
public class Competency {
    private Long id;
    private String name;

    public Competency() {}
    public Competency(Long id, String name) { this.id = id; this.name = name; }
    public Long getId() { return id; }
    public String getName() { return name; }
}
