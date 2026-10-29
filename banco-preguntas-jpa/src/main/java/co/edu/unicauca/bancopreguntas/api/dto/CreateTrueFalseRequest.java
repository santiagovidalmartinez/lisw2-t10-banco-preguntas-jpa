package co.edu.unicauca.bancopreguntas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Petición para el caso de uso 1 (crear pregunta de verdadero/falso). */
public class CreateTrueFalseRequest {
    @NotBlank public String statement;
    @NotNull public Long authorId;
    @NotNull public Boolean expectedAnswer;
}
