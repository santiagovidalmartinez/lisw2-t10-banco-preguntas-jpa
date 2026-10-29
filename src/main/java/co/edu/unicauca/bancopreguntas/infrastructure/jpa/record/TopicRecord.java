package co.edu.unicauca.bancopreguntas.infrastructure.jpa.record;

import jakarta.persistence.*;

/** Tema académico. */
@Entity
@Table(name = "topics")
public class TopicRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected TopicRecord() {}
    public TopicRecord(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
}
