package co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository;

import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.CompetencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringCompetencyDataRepository extends JpaRepository<CompetencyRecord, Long> {
    List<CompetencyRecord> findByIdIn(List<Long> ids);
}
