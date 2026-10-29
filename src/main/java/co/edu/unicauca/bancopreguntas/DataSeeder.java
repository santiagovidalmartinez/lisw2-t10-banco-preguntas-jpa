package co.edu.unicauca.bancopreguntas;

import co.edu.unicauca.bancopreguntas.domain.Role;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.CompetencyRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.TopicRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringCompetencyDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringTopicDataRepository;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.repository.SpringUserDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * Carga datos de prueba al arrancar (usuarios con cada rol, un tema y una competencia) para poder
 * probar los endpoints de inmediato con Postman/curl, igual que se hace con el demo de referencia.
 */
@Component
public class DataSeeder implements CommandLineRunner {
    private final SpringUserDataRepository users;
    private final SpringTopicDataRepository topics;
    private final SpringCompetencyDataRepository competencies;

    public DataSeeder(SpringUserDataRepository users, SpringTopicDataRepository topics, SpringCompetencyDataRepository competencies) {
        this.users = users; this.topics = topics; this.competencies = competencies;
    }

    @Override
    public void run(String... args) {
        UserRecord autor = users.save(new UserRecord("Ana Autora", "ana@unicauca.edu.co", Set.of(Role.AUTHOR)));
        UserRecord revisor = users.save(new UserRecord("Raúl Revisor", "raul@unicauca.edu.co", Set.of(Role.REVIEWER)));
        UserRecord docente = users.save(new UserRecord("Diego Docente", "diego@unicauca.edu.co", Set.of(Role.TEACHER)));
        TopicRecord tema = topics.save(new TopicRecord("Álgebra"));
        CompetencyRecord competencia = competencies.save(new CompetencyRecord("Razonamiento Cuantitativo"));

        System.out.println("=== Datos de prueba cargados ===");
        System.out.println("autor id=" + autor.getId() + " (" + autor.getEmail() + ", rol AUTHOR)");
        System.out.println("revisor id=" + revisor.getId() + " (" + revisor.getEmail() + ", rol REVIEWER)");
        System.out.println("docente id=" + docente.getId() + " (" + docente.getEmail() + ", rol TEACHER)");
        System.out.println("tema id=" + tema.getId() + " (" + tema.getName() + ")");
        System.out.println("competencia id=" + competencia.getId() + " (" + competencia.getName() + ")");
    }
}
