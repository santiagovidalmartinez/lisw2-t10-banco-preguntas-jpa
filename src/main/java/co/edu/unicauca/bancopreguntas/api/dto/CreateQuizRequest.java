package co.edu.unicauca.bancopreguntas.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Petición para el caso de uso 5 (crear cuestionario). */
public class CreateQuizRequest {
    @NotBlank public String title;
    @NotNull public Long teacherId;
    @NotEmpty public List<Long> questionIds;
}
