package co.edu.unicauca.bancopreguntas.api;

import co.edu.unicauca.bancopreguntas.api.dto.CreateQuizRequest;
import co.edu.unicauca.bancopreguntas.api.dto.QuizResponse;
import co.edu.unicauca.bancopreguntas.application.QuizService;
import co.edu.unicauca.bancopreguntas.domain.Quiz;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Expone el caso de uso 5 (crear cuestionario). */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService service;

    public QuizController(QuizService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<QuizResponse> create(@Valid @RequestBody CreateQuizRequest request) {
        Quiz quiz = service.createQuiz(request.title, request.teacherId, request.questionIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizResponse.from(quiz));
    }
}
