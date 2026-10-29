package co.edu.unicauca.bancopreguntas;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica, leyendo directamente el código fuente (sin reflexión ni bytecode), que se respeta la
 * arquitectura limpia exigida por el taller:
 *  - domain no importa jakarta.persistence ni org.springframework (ni siquiera para anotaciones).
 *  - application no importa jakarta.persistence ni org.springframework.data (JPA/Spring Data).
 *  - api no importa infrastructure directamente (solo a través de las interfaces de domain).
 * Se corre con `mvn test` igual que las demás pruebas; no depende de ArchUnit ni de ninguna librería
 * externa adicional, por eso puede ejecutarse incluso sin acceso a un repositorio de dependencias.
 */
class DomainPurityTest {

    private static final Path SRC_MAIN = Paths.get("src", "main", "java", "co", "edu", "unicauca", "bancopreguntas");

    private List<String> javaFilesUnder(String subpackage) throws IOException {
        Path dir = SRC_MAIN.resolve(subpackage);
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(p -> p.toString().endsWith(".java")).map(this::readFile).toList();
        }
    }

    private String readFile(Path p) {
        try { return Files.readString(p); } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    @Test
    void dominioNoImportaJpaNiSpring() throws IOException {
        for (String contenido : javaFilesUnder("domain")) {
            assertTrue(!contenido.contains("import jakarta.persistence"), "El dominio no debe importar jakarta.persistence");
            assertTrue(!contenido.contains("import org.springframework"), "El dominio no debe importar Spring");
        }
    }

    @Test
    void aplicacionNoImportaJpaNiSpringData() throws IOException {
        for (String contenido : javaFilesUnder("application")) {
            assertTrue(!contenido.contains("import jakarta.persistence"), "application no debe importar jakarta.persistence");
            assertTrue(!contenido.contains("import org.springframework.data"), "application no debe importar Spring Data");
            assertTrue(!contenido.contains("infrastructure.jpa"), "application no debe importar infrastructure.jpa");
        }
    }

    @Test
    void apiNoImportaInfraestructuraDirectamente() throws IOException {
        for (String contenido : javaFilesUnder("api")) {
            assertTrue(!contenido.contains("import co.edu.unicauca.bancopreguntas.infrastructure"),
                "api no debe importar infrastructure directamente; debe pasar por application y domain");
        }
    }

    private static class UncheckedIOException extends RuntimeException {
        UncheckedIOException(IOException cause) { super(cause); }
    }
}
