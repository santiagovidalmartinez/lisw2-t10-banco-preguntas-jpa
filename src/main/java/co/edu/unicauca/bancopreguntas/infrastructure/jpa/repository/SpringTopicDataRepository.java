package co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository;

import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.TopicRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringTopicDataRepository extends JpaRepository<TopicRecord, Long> {}
