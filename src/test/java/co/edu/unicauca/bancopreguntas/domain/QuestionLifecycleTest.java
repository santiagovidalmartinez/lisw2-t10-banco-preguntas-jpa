package co.edu.unicauca.bancopreguntas.domain;

import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de dominio puro (sin JPA/Spring) para el ciclo de vida y las reglas de Question y sus subtipos. */
class QuestionLifecycleTest {

    private MultipleChoiceQuestion preguntaCompletaMC() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Capital de Colombia?", 10L);
        p.addOption(new AnswerOption(null, "Bogotá", true));
        p.addOption(new AnswerOption(null, "Medellín", false));
        p.addOption(new AnswerOption(null, "Cali", false));
        p.addOption(new AnswerOption(null, "Cartagena", false));
        p.addOption(new AnswerOption(null, "Barranquilla", false));
        p.classify(100L, Set.of(200L));
        return p;
    }

    @Test
    void cicloDeVidaCompletoHastaPublicada() {
        MultipleChoiceQuestion p = preguntaCompletaMC();
        assertEquals(QuestionStatus.DRAFT, p.getStatus());
        p.submitForReview();
        assertEquals(QuestionStatus.PENDING_REVIEW, p.getStatus());
        p.startReview();
        assertEquals(QuestionStatus.IN_REVIEW, p.getStatus());
        p.approve("Se ve bien");
        assertEquals(QuestionStatus.APPROVED, p.getStatus());
        p.publish();
        assertEquals(QuestionStatus.PUBLISHED, p.getStatus());
        p.archive();
        assertEquals(QuestionStatus.ARCHIVED, p.getStatus());
    }

    @Test
    void rechazoDevuelveADraft() {
        MultipleChoiceQuestion p = preguntaCompletaMC();
        p.submitForReview();
        p.startReview();
        p.reject("Falta claridad en el enunciado");
        assertEquals(QuestionStatus.DRAFT, p.getStatus());
        assertEquals("Falta claridad en el enunciado", p.getMetadata().getNotes());
    }

    @Test
    void transicionInvalidaLanzaExcepcion() {
        // Una pregunta recién creada está en DRAFT; approve() solo es válido desde IN_REVIEW.
        MultipleChoiceQuestion p = preguntaCompletaMC();
        assertThrows(BusinessRuleViolationException.class, () -> p.approve("no debería poder aprobarse aún"));
    }

    @Test
    void enviarARevisionSinTemaFalla() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Pregunta sin tema?", 10L);
        p.addOption(new AnswerOption(null, "A", true));
        p.addOption(new AnswerOption(null, "B", false));
        p.addOption(new AnswerOption(null, "C", false));
        p.addOption(new AnswerOption(null, "D", false));
        p.addOption(new AnswerOption(null, "E", false));
        assertThrows(BusinessRuleViolationException.class, p::submitForReview);
    }

    @Test
    void multipleChoiceRequiereExactamenteCincoOpciones() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Pregunta incompleta?", 10L);
        p.addOption(new AnswerOption(null, "A", true));
        p.addOption(new AnswerOption(null, "B", false));
        p.classify(100L, Set.of(200L));
        assertThrows(BusinessRuleViolationException.class, p::submitForReview);
    }

    @Test
    void multipleChoiceRequiereUnaSolaOpcionCorrecta() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Dos correctas?", 10L);
        assertThrows(BusinessRuleViolationException.class, () -> {
            p.addOption(new AnswerOption(null, "A", true));
            p.addOption(new AnswerOption(null, "B", true));
        });
    }

    @Test
    void multipleChoiceNoPermiteTextosRepetidos() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Repetidas?", 10L);
        p.addOption(new AnswerOption(null, "Bogotá", true));
        assertThrows(BusinessRuleViolationException.class, () -> p.addOption(new AnswerOption(null, "Bogotá", false)));
    }

    @Test
    void multipleChoiceMaximoCincoOpciones() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Máximo?", 10L);
        p.addOption(new AnswerOption(null, "A", true));
        p.addOption(new AnswerOption(null, "B", false));
        p.addOption(new AnswerOption(null, "C", false));
        p.addOption(new AnswerOption(null, "D", false));
        p.addOption(new AnswerOption(null, "E", false));
        assertThrows(BusinessRuleViolationException.class, () -> p.addOption(new AnswerOption(null, "F", false)));
    }

    @Test
    void trueFalseSoloNecesitaTemaYCompetencia() {
        TrueFalseQuestion p = new TrueFalseQuestion(1L, "¿Java es orientado a objetos?", 10L, true);
        p.classify(100L, Set.of(200L));
        assertDoesNotThrow(p::submitForReview);
    }

    @Test
    void soloDraftPuedeReclasificarse() {
        MultipleChoiceQuestion p = preguntaCompletaMC();
        p.submitForReview();
        assertThrows(BusinessRuleViolationException.class, () -> p.classify(999L, Set.of(1L)));
    }

    @Test
    void clasificarSinCompetenciasFalla() {
        MultipleChoiceQuestion p = new MultipleChoiceQuestion(1L, "¿Sin competencias?", 10L);
        assertThrows(BusinessRuleViolationException.class, () -> p.classify(100L, Set.of()));
    }
}
