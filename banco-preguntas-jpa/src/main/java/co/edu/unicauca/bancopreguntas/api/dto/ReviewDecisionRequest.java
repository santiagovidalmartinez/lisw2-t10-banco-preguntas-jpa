package co.edu.unicauca.bancopreguntas.api.dto;

/** Petición para aprobar/rechazar una pregunta en revisión. */
public class ReviewDecisionRequest {
    public Long reviewerId;
    public String notes;
}
