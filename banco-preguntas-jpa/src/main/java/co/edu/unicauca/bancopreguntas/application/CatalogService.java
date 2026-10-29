package co.edu.unicauca.bancopreguntas.application;

import co.edu.unicauca.bancopreguntas.domain.Competency;
import co.edu.unicauca.bancopreguntas.domain.Topic;
import co.edu.unicauca.bancopreguntas.domain.repository.CompetencyRepository;
import co.edu.unicauca.bancopreguntas.domain.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Consultas de catálogo (temas y competencias) usadas por la API para poblar formularios. */
@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final TopicRepository topics;
    private final CompetencyRepository competencies;

    public CatalogService(TopicRepository topics, CompetencyRepository competencies) {
        this.topics = topics; this.competencies = competencies;
    }

    public List<Topic> listTopics() { return topics.findAll(); }
    public List<Competency> listCompetencies() { return competencies.findAll(); }
}
