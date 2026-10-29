package co.edu.unicauca.bancopreguntas.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/** Petición para el caso de uso 3 (clasificar: tema + competencias). */
public class ClassifyRequest {
    @NotNull public Long topicId;
    @NotEmpty public Set<Long> competencyIds;
}
