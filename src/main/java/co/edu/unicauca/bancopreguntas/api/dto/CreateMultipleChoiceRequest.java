package co.edu.unicauca.bancopreguntas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Petición para el caso de uso 1 (crear pregunta de selección múltiple). */
public class CreateMultipleChoiceRequest {
    @NotBlank public String statement;
    @NotNull public Long authorId;
    @NotNull @Size(min = 5, max = 5, message = "Se requieren exactamente 5 opciones")
    public List<OptionRequest> options;

    public static class OptionRequest {
        @NotBlank public String text;
        public boolean correct;
    }
}
