# Taller 10 — Persistencia con JPA y Arquitectura Limpia

**Proyecto:** Banco de Preguntas Saber PRO — Universidad del Cauca, Ingeniería de Software II
**Autor:** Santiago Vidal Martínez

## ¿Qué hace este proyecto?

Implementa la Parte III del Taller 10: el esquema de persistencia relacional del Banco de Preguntas,
siguiendo la arquitectura limpia del proyecto demo `jpa-clean-persistence-demo` (Parte I/II del taller).
Las clases de negocio (paquete `domain`) son Java puro, sin ninguna anotación de `jakarta.persistence`
ni dependencia de Spring; la jerarquía JPA (`infrastructure.jpa.record`) es un modelo completamente
separado, traducido en ambos sentidos por `QuestionMapper`/`CatalogMapper`.

| Paquete | Contenido |
|---|---|
| `domain` | `Question` (abstracta), `MultipleChoiceQuestion`, `TrueFalseQuestion`, `AnswerOption`, `QuestionMetadata`, `User`, `Role`, `Topic`, `Competency`, `Quiz`. Reglas de negocio y ciclo de vida. |
| `domain.repository` | Contratos de persistencia (`QuestionRepository`, `UserRepository`, `TopicRepository`, `CompetencyRepository`, `QuizRepository`), sin JPA ni Spring Data. |
| `application` | Casos de uso: `QuestionService`, `ReviewService`, `QuizService`, `CatalogService`. Solo dependen de las interfaces de `domain.repository`. |
| `infrastructure.jpa.record` | Entidades JPA (`QuestionRecord` + `@Inheritance(JOINED)`, `MultipleChoiceQuestionRecord`, `TrueFalseQuestionRecord`, `AnswerOptionRecord`, `QuestionMetadataRecord`, `UserRecord`, `TopicRecord`, `CompetencyRecord`, `QuizRecord`). |
| `infrastructure.jpa.repository` | Interfaces Spring Data (`SpringQuestionDataRepository`, etc.). |
| `infrastructure.jpa` | `QuestionMapper`, `CatalogMapper` y las implementaciones concretas `*RepositoryJpa` de los contratos de dominio. |
| `api` | Controladores REST (`QuestionController`, `ReviewController`, `QuizController`), DTOs y `ApiExceptionHandler` (404/409/400). |

## Los cinco casos de uso

1. **Crear pregunta**: `POST /api/questions/multiple-choice` o `/true-false`. Autor con rol `AUTHOR`; selección múltiple exige exactamente 5 opciones.
2. **Revisar**: `POST /api/reviews/{id}/start`, `/approve`, `/reject`, `/publish`, `/archive`. Revisor con rol `REVIEWER`.
3. **Clasificar**: `PUT /api/questions/{id}/classification`. Solo en `DRAFT`; tema y competencias deben existir.
4. **Enviar a revisión**: `POST /api/questions/{id}/submit-for-review`. El dominio valida las condiciones mínimas (tema, competencias, reglas del tipo); si falla no cambia el estado (409).
5. **Crear cuestionario**: `POST /api/quizzes`. Docente con rol `TEACHER`; todas las preguntas deben estar `PUBLISHED`, sin repetir.

Ciclo de vida: `DRAFT → PENDING_REVIEW → IN_REVIEW → APPROVED → PUBLISHED → ARCHIVED`; el rechazo devuelve `IN_REVIEW → DRAFT`. Cualquier otra transición lanza `BusinessRuleViolationException` → HTTP 409.

## Diagramas (`docs/`)

- `diagrama_dominio.png` — Figura 2, UML de clases del dominio.
- `modelo_relacional.png` — Figura 3, modelo relacional derivado del mapeo JPA.
- `capas.png` — Figura 4, capas y dirección de las dependencias.

## Lo que necesitas para correrlo

Igual que el Taller 09: este proyecto se compila y se prueba con Maven, lo que requiere **internet** para
descargar Spring Boot, Spring Data JPA y H2 la primera vez. No necesita Docker (H2 es en memoria).

## Cómo correrlo paso a paso

### 1. Compilar y correr las pruebas

```bash
mvn clean test
```

Esto corre **todas** las pruebas: las de dominio puro (`QuestionLifecycleTest`, `QuizTest`,
`DomainPurityTest`), las de aplicación con repositorios en memoria (`QuestionServiceTest`,
`ReviewAndQuizServiceTest`) y las de infraestructura/API con H2 real (`QuestionRepositoryJpaTest`,
`SchemaConstraintsTest`, `QuizRepositoryJpaTest`, `QuestionBankApiTest`).

### 2. Levantar la aplicación

```bash
mvn spring-boot:run
```

Al arrancar, `DataSeeder` imprime en consola los IDs de los usuarios, tema y competencia de prueba
(autor con rol AUTHOR, revisor con rol REVIEWER, docente con rol TEACHER). Úsalos en las peticiones.

### 3. Probar con curl (reemplaza los IDs por los que imprimió la consola)

```bash
curl -X POST localhost:8080/api/questions/multiple-choice -H "Content-Type: application/json" -d '{
  "statement":"¿Cuánto es 2+2?","authorId":1,
  "options":[{"text":"4","correct":true},{"text":"3","correct":false},{"text":"5","correct":false},{"text":"6","correct":false},{"text":"7","correct":false}]
}'

curl -X PUT localhost:8080/api/questions/1/classification -H "Content-Type: application/json" -d '{"topicId":1,"competencyIds":[1]}'
curl -X POST localhost:8080/api/questions/1/submit-for-review
curl -X POST localhost:8080/api/reviews/1/start   -H "Content-Type: application/json" -d '{"reviewerId":2}'
curl -X POST localhost:8080/api/reviews/1/approve -H "Content-Type: application/json" -d '{"notes":"Correcta"}'
curl -X POST localhost:8080/api/reviews/1/publish
curl -X POST localhost:8080/api/quizzes -H "Content-Type: application/json" -d '{"title":"Parcial 1","teacherId":3,"questionIds":[1]}'
```

### 4. Ver el esquema real en la consola H2

Abre **http://localhost:8080/h2-console** (JDBC URL `jdbc:h2:mem:bancopreguntasdb`, usuario `sa`, sin contraseña).

```sql
SCRIPT NODATA;
SELECT * FROM questions;
SELECT * FROM multiple_choice_questions;
SELECT * FROM answer_options;
SELECT * FROM question_competencies;
```

### 5. Evidencias para el informe

- Resumen de `mvn clean test` (Tests run: ..., Failures: 0) o una captura.
- Capturas de la consola H2 mostrando las tablas `questions`, `multiple_choice_questions`,
  `true_false_questions`, `answer_options`, `question_competencies`.
- Capturas de los endpoints respondiendo (curl o Postman).

## Estructura del proyecto

```
banco-preguntas-jpa/
├── docs/                          # Diagramas (Figuras 2, 3 y 4)
├── pom.xml
└── src/
    ├── main/java/co/edu/unicauca/bancopreguntas/
    │   ├── domain/                # Java puro, sin JPA ni Spring
    │   ├── application/           # Casos de uso (Spring, sin JPA)
    │   ├── infrastructure/jpa/    # Records, Spring Data, mappers, *RepositoryJpa
    │   └── api/                   # Controladores REST, DTOs, manejo de errores
    └── test/java/co/edu/unicauca/bancopreguntas/
        ├── domain/                # Pruebas puras (corren sin Spring)
        ├── application/           # Pruebas con repositorios en memoria
        ├── infrastructure/jpa/    # @DataJpaTest con H2 real
        └── api/                   # @SpringBootTest + MockMvc
```
