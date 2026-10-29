package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.CompetencyRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.TopicRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración contra H2 real (@DataJpaTest arranca un contexto JPA completo, sin mocks).
 * No se pudo ejecutar en el entorno de preparación de este taller por falta de acceso a Maven Central
 * (Spring Boot, H2 y sus dependencias no se pudieron descargar); se ejecuta con `mvn test` en un equipo
 * con internet, igual que el resto del proyecto.
 */
@DataJpaTest
@Import({QuestionRepositoryJpa.class, UserRepositoryJpa.class, TopicRepositoryJpa.class, CompetencyRepositoryJpa.class})
class QuestionRepositoryJpaTest {
    @Autowired private QuestionRepositoryJpa questionRepository;
    @Autowired private SpringUserDataRepository userData;
    @Autowired private SpringTopicDataRepository topicData;
    @Autowired private SpringCompetencyDataRepository competencyData;

    private Long autorId, temaId, competenciaId;

    @BeforeEach
    void setUp() {
        autorId = userData.save(new UserRecord("Ana Autora", "ana@unicauca.edu.co", Set.of(Role.AUTHOR))).getId();
        temaId = topicData.save(new TopicRecord("Álgebra")).getId();
        competenciaId = competencyData.save(new CompetencyRecord("Razonamiento Cuantitativo")).getId();
    }

    private MultipleChoiceQuestion preguntaMultipleChoice() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(null, "¿Cuánto es 2+2?", autorId);
        p.addOption(new AnswerOption(null, "4", true));
        p.addOption(new AnswerOption(null, "3", false));
        p.addOption(new AnswerOption(null, "5", false));
        p.addOption(new AnswerOption(null, "6", false));
        p.addOption(new AnswerOption(null, "7", false));
        return p;
    }

    @Test
    void guardaYRecuperaMultipleChoiceConOpcionesEnOrden() {
        MultipleChoiceQuestion p = preguntaMultipleChoice();
        Question guardada = questionRepository.save(p);
        assertNotNull(guardada.getId());

        Question recuperada = questionRepository.findById(guardada.getId()).orElseThrow();
        assertTrue(recuperada instanceof MultipleChoiceQuestion);
        MultipleChoiceQuestion mc = (MultipleChoiceQuestion) recuperada;
        assertEquals(5, mc.getOptions().size());
        assertEquals("4", mc.getOptions().get(0).getText());
        assertEquals(1, mc.getOptions().stream().filter(AnswerOption::isCorrect).count());
    }

    @Test
    void guardaYRecuperaTrueFalse() {
        TrueFalseQuestion p = new TrueFalseQuestion(null, "¿Java es orientado a objetos?", autorId, true);
        Question guardada = questionRepository.save(p);
        Question recuperada = questionRepository.findById(guardada.getId()).orElseThrow();
        assertTrue(recuperada instanceof TrueFalseQuestion);
        assertTrue(((TrueFalseQuestion) recuperada).getExpectedAnswer());
    }

    @Test
    void guardaTemaYCompetenciasYLosRecupera() {
        MultipleChoiceQuestion p = preguntaMultipleChoice();
        p.classify(temaId, Set.of(competenciaId));
        Question guardada = questionRepository.save(p);
        Question recuperada = questionRepository.findById(guardada.getId()).orElseThrow();
        assertEquals(temaId, recuperada.getTopicId());
        assertEquals(Set.of(competenciaId), recuperada.getCompetencyIds());
    }

    @Test
    void actualizaEstadoYMetadatosAlAprobar() {
        MultipleChoiceQuestion p = preguntaMultipleChoice();
        p.classify(temaId, Set.of(competenciaId));
        Question guardada = questionRepository.save(p);
        guardada.submitForReview();
        guardada.startReview();
        guardada.approve("Pregunta clara y bien formulada");
        Question actualizada = questionRepository.save(guardada);

        Question recuperada = questionRepository.findById(actualizada.getId()).orElseThrow();
        assertEquals(QuestionStatus.APPROVED, recuperada.getStatus());
        assertNotNull(recuperada.getMetadata());
        assertEquals("Pregunta clara y bien formulada", recuperada.getMetadata().getNotes());
        assertNotNull(recuperada.getMetadata().getLastReviewedAt());
    }

    @Test
    void consultaPorAutorEstadoYCompetencia() {
        MultipleChoiceQuestion p = preguntaMultipleChoice();
        p.classify(temaId, Set.of(competenciaId));
        Question guardada = questionRepository.save(p);
        guardada.submitForReview();
        questionRepository.save(guardada);

        List<Question> porAutor = questionRepository.findByAuthor(autorId);
        assertEquals(1, porAutor.size());

        List<Question> porEstado = questionRepository.findByStatus(QuestionStatus.PENDING_REVIEW);
        assertEquals(1, porEstado.size());

        // Aún no está PUBLISHED: no debe aparecer en la consulta de publicadas por competencia.
        List<Question> publicadasPorCompetencia = questionRepository.findPublishedByCompetency(competenciaId);
        assertTrue(publicadasPorCompetencia.isEmpty());
    }

    @Test
    void buscarVariosPorIdYEliminar() {
        Question p1 = questionRepository.save(preguntaMultipleChoice());
        Question p2 = questionRepository.save(preguntaMultipleChoice());
        List<Question> encontradas = questionRepository.findByIds(List.of(p1.getId(), p2.getId()));
        assertEquals(2, encontradas.size());

        questionRepository.deleteById(p1.getId());
        assertTrue(questionRepository.findById(p1.getId()).isEmpty());
    }

    @Test
    void jerarquiaJoinedGeneraTablaBaseYTablaDeSubtipo() {
        // Con JOINED, questions guarda la fila base y multiple_choice_questions guarda las columnas propias.
        Question p = questionRepository.save(preguntaMultipleChoice());
        Question recuperada = questionRepository.findById(p.getId()).orElseThrow();
        assertEquals(5, ((MultipleChoiceQuestion) recuperada).getOptions().size());
    }
}
