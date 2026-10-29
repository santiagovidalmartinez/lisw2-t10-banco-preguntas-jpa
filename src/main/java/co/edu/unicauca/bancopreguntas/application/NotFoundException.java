package co.edu.unicauca.bancopreguntas.application;

/** Se referenció un identificador que no existe (autor, tema, competencia, pregunta, etc.). Mapea a HTTP 404. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
