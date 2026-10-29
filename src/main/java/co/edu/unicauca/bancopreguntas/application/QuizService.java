package co.edu.unicauca.bancopreguntas.application;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.domain.repository.QuestionRepository;
import co.edu.unicauca.bancopreguntas.domain.repository.QuizRepository;
import co.edu.unicauca.bancopreguntas.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Caso de uso 5: crear un cuestionario con preguntas PUBLISHED, sin repetidas. */
@Service
@Transactional
public class QuizService {
    private final QuizRepository quizzes;
    private final QuestionRepository questions;
    private final UserRepository users;

    public QuizService(QuizRepository quizzes, QuestionRepository questions, UserRepository users) {
        this.quizzes = quizzes; this.questions = questions; this.users = users;
    }

    /** Si una de las preguntas falla la validación, no se guarda nada (transaccional en la implementación JPA). */
    public Quiz createQuiz(String title, Long teacherId, List<Long> questionIds) {
        User docente = users.findById(teacherId).orElseThrow(() -> new NotFoundException("Docente no encontrado: " + teacherId));
        if (!docente.hasRole(Role.TEACHER))
            throw new BusinessRuleViolationException("El usuario " + teacherId + " no tiene el rol TEACHER.");
        List<Question> preguntas = questions.findByIds(questionIds);
        if (preguntas.size() != questionIds.size())
            throw new NotFoundException("Una o más preguntas no existen: " + questionIds);
        Quiz quiz = Quiz.create(null, title, teacherId, preguntas);
        return quizzes.save(quiz);
    }
}
