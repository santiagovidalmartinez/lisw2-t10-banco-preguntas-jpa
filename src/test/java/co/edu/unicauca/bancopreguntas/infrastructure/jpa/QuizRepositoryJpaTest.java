package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.TopicRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.CompetencyRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringTopicDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringCompetencyDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringUserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración de QuizRepositoryJpa (relación N:M ordenada con @OrderColumn).
 * No se pudo ejecutar aquí por falta de acceso a Maven Central; se ejecuta con `mvn test`.
 */
@DataJpaTest
@Import({QuestionRepositoryJpa.class, QuizRepositoryJpa.class, UserRepositoryJpa.class})
class QuizRepositoryJpaTest {
    @Autowired private QuestionRepositoryJpa questionRepository;
    @Autowired private QuizRepositoryJpa quizRepository;
    @Autowired private SpringUserDataRepository userData;

    @Autowired private SpringTopicDataRepository topicData;
    @Autowired private SpringCompetencyDataRepository competencyData;
    private Long autorId, docenteId, temaId, competenciaId;

    @BeforeEach
    void setUp() {
        autorId = userData.save(new UserRecord("Ana Autora", "ana.quiz@unicauca.edu.co", Set.of(Role.AUTHOR))).getId();
        docenteId = userData.save(new UserRecord("Diego Docente", "diego.quiz@unicauca.edu.co", Set.of(Role.TEACHER))).getId();
        temaId = topicData.save(new TopicRecord("Algebra quiz")).getId();
        competenciaId = competencyData.save(new CompetencyRecord("Razonamiento quiz")).getId();
    }

    private Question preguntaPublicada(String enunciado) {
        TrueFalseQuestion p = new TrueFalseQuestion(null, enunciado, autorId, true);
        Question guardada = questionRepository.save(p);
        guardada.classify(temaId, Set.of(competenciaId)); questionRepository.save(guardada);
        guardada.submitForReview(); questionRepository.save(guardada);
        guardada.startReview(); questionRepository.save(guardada);
        guardada.approve("ok"); questionRepository.save(guardada);
        guardada.publish(); questionRepository.save(guardada);
        return guardada;
    }

    @Test
    void guardaCuestionarioConOrdenPreservado() {
        Question p1 = preguntaPublicada("Pregunta A");
        Question p2 = preguntaPublicada("Pregunta B");
        Quiz quiz = Quiz.create(null, "Parcial 1", docenteId, List.of(p1, p2));
        Quiz guardado = quizRepository.save(quiz);

        Quiz recuperado = quizRepository.findById(guardado.getId()).orElseThrow();
        assertEquals(List.of(p1.getId(), p2.getId()), recuperado.getQuestionIds());
    }

    @Test
    void referenciaAPreguntaInexistenteNoSeGuarda() {
        Quiz quiz = Quiz.reconstruct(null, "Parcial inválido", docenteId, List.of(999999L));
        // findByIdIn no encuentra 999999L, así que el cuestionario quedaría sin preguntas: lo detecta
        // QuizService antes de llegar aquí (NotFoundException); a nivel de repositorio se guarda vacío.
        Quiz guardado = quizRepository.save(quiz);
        assertTrue(quizRepository.findById(guardado.getId()).orElseThrow().getQuestionIds().isEmpty());
    }
}
