package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.QuestionStatus;
import co.edu.unicauca.bancopreguntas.domain.Role;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.transaction.TestTransaction;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que el esquema generado por las anotaciones JPA realmente impone las restricciones que el
 * dominio da por sentadas (FK NOT NULL, UNIQUE, cascada). No se pudo ejecutar aquí por falta de acceso
 * a Maven Central; se ejecuta con `mvn test`.
 */
@DataJpaTest
class SchemaConstraintsTest {
    @Autowired private EntityManager em;

    @Test
    void preguntaSinAutorFalla() {
        TrueFalseQuestionRecord pregunta = new TrueFalseQuestionRecord("Pregunta sin autor", QuestionStatus.DRAFT, null, true);
        assertThrows(Exception.class, () -> { em.persist(pregunta); em.flush(); });
    }

    @Test
    void opcionSinPreguntaFalla() {
        AnswerOptionRecord opcion = new AnswerOptionRecord("Opción huérfana", true);
        assertThrows(Exception.class, () -> { em.persist(opcion); em.flush(); });
    }

    @Test
    void emailDeUsuarioEsUnico() {
        em.persist(new UserRecord("Autor 1", "repetido@unicauca.edu.co", Set.of(Role.AUTHOR)));
        em.flush();
        UserRecord duplicado = new UserRecord("Autor 2", "repetido@unicauca.edu.co", Set.of(Role.AUTHOR));
        assertThrows(Exception.class, () -> { em.persist(duplicado); em.flush(); });
    }

    @Test
    void metadatosNoPuedenCompartirseEntreDosPreguntas() {
        UserRecord autor = new UserRecord("Autora", "autora.meta@unicauca.edu.co", Set.of(Role.AUTHOR));
        em.persist(autor); em.flush();

        QuestionMetadataRecord metadata = new QuestionMetadataRecord(null, 2, "nota", null);
        em.persist(metadata); em.flush();

        TrueFalseQuestionRecord p1 = new TrueFalseQuestionRecord("Pregunta 1", QuestionStatus.DRAFT, autor, true);
        p1.setMetadata(metadata);
        TrueFalseQuestionRecord p2 = new TrueFalseQuestionRecord("Pregunta 2", QuestionStatus.DRAFT, autor, false);
        p2.setMetadata(metadata); // mismo metadata_id: la columna unique=true de la FK lo impide

        em.persist(p1); em.flush();
        assertThrows(Exception.class, () -> { em.persist(p2); em.flush(); });
    }

    @Test
    void coleccionLazyFueraDeSesionLanzaExcepcion() {
        UserRecord autor = new UserRecord("Autora LAZY", "lazy@unicauca.edu.co", Set.of(Role.AUTHOR));
        em.persist(autor);
        MultipleChoiceQuestionRecord pregunta = new MultipleChoiceQuestionRecord("¿Pregunta?", QuestionStatus.DRAFT, autor);
        pregunta.addOption(new AnswerOptionRecord("A", true));
        em.persist(pregunta);
        em.flush();
        Long id = pregunta.getId();
        em.clear(); // simula salir de la sesión/transacción sin haber inicializado la colección

        MultipleChoiceQuestionRecord recargada = em.find(MultipleChoiceQuestionRecord.class, id);
        em.detach(recargada);
        assertThrows(LazyInitializationException.class, () -> recargada.getOptions().size());
    }
}
