package co.edu.unicauca.bancopreguntas.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de dominio puro para Quiz.create (caso de uso 5). */
class QuizTest {

    private Question preguntaPublicada(long id) {
        TrueFalseQuestion p = new TrueFalseQuestion(id, "Pregunta " + id, 10L, true);
        p.classify(100L, Set.of(200L));
        p.submitForReview();
        p.startReview();
        p.approve("ok");
        p.publish();
        return p;
    }

    @Test
    void creaCuestionarioConPreguntasPublicadas() {
        Quiz quiz = Quiz.create(null, "Parcial 1", 20L, List.of(preguntaPublicada(1), preguntaPublicada(2)));
        assertEquals("Parcial 1", quiz.getTitle());
        assertEquals(List.of(1L, 2L), quiz.getQuestionIds());
    }

    @Test
    void rechazaPreguntaNoPublicada() {
        TrueFalseQuestion borrador = new TrueFalseQuestion(5L, "Pregunta en borrador", 10L, false);
        assertThrows(BusinessRuleViolationException.class,
            () -> Quiz.create(null, "Parcial 2", 20L, List.of(preguntaPublicada(1), borrador)));
    }

    @Test
    void rechazaPreguntasRepetidas() {
        Question p = preguntaPublicada(1);
        assertThrows(BusinessRuleViolationException.class,
            () -> Quiz.create(null, "Parcial 3", 20L, List.of(p, p)));
    }

    @Test
    void rechazaListaVacia() {
        assertThrows(BusinessRuleViolationException.class, () -> Quiz.create(null, "Vacío", 20L, List.of()));
    }

    @Test
    void rechazaTituloEnBlanco() {
        assertThrows(BusinessRuleViolationException.class,
            () -> Quiz.create(null, "  ", 20L, List.of(preguntaPublicada(1))));
    }
}
