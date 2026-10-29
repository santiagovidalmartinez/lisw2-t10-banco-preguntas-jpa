package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.Competency;
import co.edu.unicauca.bancopreguntas.domain.repository.CompetencyRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringCompetencyDataRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class CompetencyRepositoryJpa implements CompetencyRepository {
    private final SpringCompetencyDataRepository data;

    public CompetencyRepositoryJpa(SpringCompetencyDataRepository data) { this.data = data; }

    @Override
    public Optional<Competency> findById(Long id) { return data.findById(id).map(CatalogMapper::toDomain); }

    @Override
    public List<Competency> findAllByIds(List<Long> ids) { return data.findByIdIn(ids).stream().map(CatalogMapper::toDomain).toList(); }

    @Override
    public List<Competency> findAll() { return data.findAll().stream().map(CatalogMapper::toDomain).toList(); }
}
