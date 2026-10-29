package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.Competency;
import co.edu.unicauca.bancopreguntas.domain.Topic;
import co.edu.unicauca.bancopreguntas.domain.User;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.CompetencyRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.TopicRecord;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.UserRecord;

/** Convierte entre los objetos de dominio User/Topic/Competency y sus records JPA. */
public final class CatalogMapper {
    private CatalogMapper() {}

    public static User toDomain(UserRecord record) {
        return new User(record.getId(), record.getFullName(), record.getEmail(), record.getRoles());
    }

    public static UserRecord toRecord(User user) {
        return new UserRecord(user.getFullName(), user.getEmail(), user.getRoles());
    }

    public static Topic toDomain(TopicRecord record) {
        return new Topic(record.getId(), record.getName());
    }

    public static Competency toDomain(CompetencyRecord record) {
        return new Competency(record.getId(), record.getName());
    }
}
