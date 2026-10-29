package co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository;

import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.QuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringQuizDataRepository extends JpaRepository<QuizRecord, Long> {}
