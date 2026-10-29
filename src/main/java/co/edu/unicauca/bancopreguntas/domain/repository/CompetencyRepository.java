package co.edu.unicauca.bancopreguntas.domain.repository;

import co.edu.unicauca.bancopreguntas.domain.Competency;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de competencias. */
public interface CompetencyRepository {
    Optional<Competency> findById(Long id);
    List<Competency> findAllByIds(List<Long> ids);
    List<Competency> findAll();
}
