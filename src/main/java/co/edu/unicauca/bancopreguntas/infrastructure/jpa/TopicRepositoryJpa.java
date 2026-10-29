package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.Topic;
import co.edu.unicauca.bancopreguntas.domain.repository.TopicRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringTopicDataRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class TopicRepositoryJpa implements TopicRepository {
    private final SpringTopicDataRepository data;

    public TopicRepositoryJpa(SpringTopicDataRepository data) { this.data = data; }

    @Override
    public Optional<Topic> findById(Long id) { return data.findById(id).map(CatalogMapper::toDomain); }

    @Override
    public java.util.List<Topic> findAll() { return data.findAll().stream().map(CatalogMapper::toDomain).toList(); }
}
