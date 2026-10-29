package co.edu.unicauca.bancopreguntas.api;

import co.edu.unicauca.bancopreguntas.domain.Role;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.CompetencyRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.TopicRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringCompetencyDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringTopicDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringUserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de extremo a extremo: HTTP -> controlador -> servicio -> repositorio JPA -> H2 en memoria.
 * No se pudo ejecutar aquí por falta de acceso a Maven Central; se ejecuta con `mvn test`.
 */
@SpringBootTest
@AutoConfigureMockMvc
class QuestionBankApiTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private SpringUserDataRepository users;
    @Autowired private SpringTopicDataRepository topics;
    @Autowired private SpringCompetencyDataRepository competencies;

    private Long autorId, revisorId, docenteId, temaId, competenciaId;

    @BeforeEach
    void setUp() {
        String u = java.util.UUID.randomUUID().toString().substring(0, 8);
        autorId = users.save(new UserRecord("Ana Autora", "api.ana." + u + "@unicauca.edu.co", Set.of(Role.AUTHOR))).getId();
        revisorId = users.save(new UserRecord("Raúl Revisor", "api.raul." + u + "@unicauca.edu.co", Set.of(Role.REVIEWER))).getId();
        docenteId = users.save(new UserRecord("Diego Docente", "api.diego." + u + "@unicauca.edu.co", Set.of(Role.TEACHER))).getId();
        temaId = topics.save(new TopicRecord("Álgebra API " + u)).getId();
        competenciaId = competencies.save(new CompetencyRecord("Razonamiento Cuantitativo API " + u)).getId();
    }

    @Test
    void flujoCompletoDeLosCincoCasosDeUso() throws Exception {
        // 1. Crear
        String crear = """
            {"statement":"¿Cuánto es 3+3?","authorId":%d,"options":[
              {"text":"6","correct":true},{"text":"5","correct":false},{"text":"4","correct":false},
              {"text":"7","correct":false},{"text":"8","correct":false}]}
            """.formatted(autorId);
        String respuesta = mockMvc.perform(post("/api/questions/multiple-choice").contentType("application/json").content(crear))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andReturn().getResponse().getContentAsString();
        Long questionId = extractId(respuesta);

        // 3. Clasificar
        String clasificar = """
            {"topicId":%d,"competencyIds":[%d]}
            """.formatted(temaId, competenciaId);
        mockMvc.perform(put("/api/questions/" + questionId + "/classification").contentType("application/json").content(clasificar))
            .andExpect(status().isOk());

        // 4. Enviar a revisión
        mockMvc.perform(post("/api/questions/" + questionId + "/submit-for-review"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));

        // 2. Revisar y aprobar
        String decision = """
            {"reviewerId":%d,"notes":"ok"}
            """.formatted(revisorId);
        mockMvc.perform(post("/api/reviews/" + questionId + "/start").contentType("application/json").content(decision))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/reviews/" + questionId + "/approve").contentType("application/json").content(decision))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
        mockMvc.perform(post("/api/reviews/" + questionId + "/publish"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // 5. Crear cuestionario
        String quiz = """
            {"title":"Parcial API","teacherId":%d,"questionIds":[%d]}
            """.formatted(docenteId, questionId);
        mockMvc.perform(post("/api/quizzes").contentType("application/json").content(quiz))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.questionIds[0]").value(questionId));
    }

    @Test
    void consultarPreguntaInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/questions/999999")).andExpect(status().isNotFound());
    }

    @Test
    void enviarARevisionSinClasificarDevuelve409() throws Exception {
        String crear = """
            {"statement":"¿Verdadero o falso?","authorId":%d,"expectedAnswer":true}
            """.formatted(autorId);
        String respuesta = mockMvc.perform(post("/api/questions/true-false").contentType("application/json").content(crear))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Long questionId = extractId(respuesta);

        mockMvc.perform(post("/api/questions/" + questionId + "/submit-for-review"))
            .andExpect(status().isConflict());
    }

    @Test
    void crearPreguntaConMenosDeCincoOpcionesDevuelve400() throws Exception {
        String crear = """
            {"statement":"¿Pregunta incompleta?","authorId":%d,"options":[
              {"text":"A","correct":true},{"text":"B","correct":false}]}
            """.formatted(autorId);
        mockMvc.perform(post("/api/questions/multiple-choice").contentType("application/json").content(crear))
            .andExpect(status().isBadRequest());
    }

    private Long extractId(String json) {
        // Extracción simple sin librería adicional de parsing: suficiente para este caso de prueba.
        String marcador = "\"id\":";
        int inicio = json.indexOf(marcador) + marcador.length();
        int fin = json.indexOf(',', inicio);
        return Long.parseLong(json.substring(inicio, fin).trim());
    }
}
