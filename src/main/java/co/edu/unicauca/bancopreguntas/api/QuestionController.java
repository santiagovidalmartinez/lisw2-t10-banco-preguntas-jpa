package co.edu.unicauca.bancopreguntas.api;

import co.edu.unicauca.bancopreguntas.api.dto.*;
import co.edu.unicauca.bancopreguntas.application.QuestionService;
import co.edu.unicauca.bancopreguntas.domain.AnswerOption;
import co.edu.unicauca.bancopreguntas.domain.Question;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Expone los casos de uso 1 (crear), 3 (clasificar) y 4 (enviar a revisión). Recibe JSON, delega en
 * QuestionService y traduce la respuesta a DTO; no contiene reglas de negocio ni accede a repositorios.
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService service;

    public QuestionController(QuestionService service) { this.service = service; }

    @PostMapping("/multiple-choice")
    public ResponseEntity<QuestionResponse> createMultipleChoice(@Valid @RequestBody CreateMultipleChoiceRequest request) {
        List<AnswerOption> opciones = request.options.stream()
            .map(o -> new AnswerOption(null, o.text, o.correct)).toList();
        Question pregunta = service.createMultipleChoice(request.statement, request.authorId, opciones);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestionResponse.from(pregunta));
    }

    @PostMapping("/true-false")
    public ResponseEntity<QuestionResponse> createTrueFalse(@Valid @RequestBody CreateTrueFalseRequest request) {
        Question pregunta = service.createTrueFalse(request.statement, request.authorId, request.expectedAnswer);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestionResponse.from(pregunta));
    }

    @GetMapping("/{id}")
    public QuestionResponse get(@PathVariable Long id) {
        return QuestionResponse.from(service.findQuestion(id));
    }

    @PutMapping("/{id}/classification")
    public QuestionResponse classify(@PathVariable Long id, @Valid @RequestBody ClassifyRequest request) {
        Question pregunta = service.classify(id, request.topicId, request.competencyIds);
        return QuestionResponse.from(pregunta);
    }

    @PostMapping("/{id}/submit-for-review")
    public QuestionResponse submitForReview(@PathVariable Long id) {
        return QuestionResponse.from(service.submitForReview(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
