package co.edu.unicauca.bancopreguntas.api;

import co.edu.unicauca.bancopreguntas.api.dto.QuestionResponse;
import co.edu.unicauca.bancopreguntas.api.dto.ReviewDecisionRequest;
import co.edu.unicauca.bancopreguntas.application.ReviewService;
import org.springframework.web.bind.annotation.*;

/** Expone el caso de uso 2 (ciclo de revisión: tomar, aprobar, rechazar, publicar, archivar). */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService service;

    public ReviewController(ReviewService service) { this.service = service; }

    @PostMapping("/{questionId}/start")
    public QuestionResponse start(@PathVariable Long questionId, @RequestBody ReviewDecisionRequest request) {
        return QuestionResponse.from(service.startReview(questionId, request.reviewerId));
    }

    @PostMapping("/{questionId}/approve")
    public QuestionResponse approve(@PathVariable Long questionId, @RequestBody ReviewDecisionRequest request) {
        return QuestionResponse.from(service.approve(questionId, request.notes));
    }

    @PostMapping("/{questionId}/reject")
    public QuestionResponse reject(@PathVariable Long questionId, @RequestBody ReviewDecisionRequest request) {
        return QuestionResponse.from(service.reject(questionId, request.notes));
    }

    @PostMapping("/{questionId}/publish")
    public QuestionResponse publish(@PathVariable Long questionId) {
        return QuestionResponse.from(service.publish(questionId));
    }

    @PostMapping("/{questionId}/archive")
    public QuestionResponse archive(@PathVariable Long questionId) {
        return QuestionResponse.from(service.archive(questionId));
    }
}
