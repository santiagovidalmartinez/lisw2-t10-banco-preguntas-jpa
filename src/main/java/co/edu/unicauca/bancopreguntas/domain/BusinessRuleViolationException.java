package co.edu.unicauca.bancopreguntas.domain;

/** Señala que una operación viola una regla de negocio del dominio (no un error técnico). */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) { super(message); }
}
