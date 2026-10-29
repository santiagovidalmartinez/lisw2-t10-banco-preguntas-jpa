package co.edu.unicauca.bancopreguntas.infrastructure.jpa;

import co.edu.unicauca.bancopreguntas.domain.*;
import co.edu.unicauca.bancopreguntas.infrastructure.jpa.record.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traduce entre la jerarquía de dominio (Question, MultipleChoiceQuestion, TrueFalseQuestion) y la
 * jerarquía JPA (QuestionRecord, MultipleChoiceQuestionRecord, TrueFalseQuestionRecord). Es necesaria
 * porque ambas jerarquías son deliberadamente distintas y ninguna debe conocer a la otra.
 */
public final class QuestionMapper {
    private QuestionMapper() {}

    /** Reconstruye el objeto de dominio completo (con su estado real) a partir del record persistido. */
    public static Question toDomain(QuestionRecord record) {
        Question pregunta;
        if (record instanceof MultipleChoiceQuestionRecord mc) {
            MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(mc.getId(), mc.getStatement(), mc.getAuthor().getId());
            for (AnswerOptionRecord opcion : mc.getOptions()) {
                mcq.addOption(new AnswerOption(opcion.getId(), opcion.getText(), opcion.isCorrect()));
            }
            pregunta = mcq;
        } else if (record instanceof TrueFalseQuestionRecord tf) {
            pregunta = new TrueFalseQuestion(tf.getId(), tf.getStatement(), tf.getAuthor().getId(), tf.isExpectedAnswer());
        } else {
            throw new IllegalStateException("Subtipo de pregunta no reconocido: " + record.getClass());
        }
        Long topicId = record.getTopic() != null ? record.getTopic().getId() : null;
        Set<Long> competencyIds = record.getCompetencies().stream().map(CompetencyRecord::getId).collect(Collectors.toCollection(HashSet::new));
        QuestionMetadata metadata = toDomainMetadata(record.getMetadata());
        pregunta.restoreState(record.getStatus(), topicId, competencyIds, metadata);
        return pregunta;
    }

    /** Crea un record nuevo (sin id) a partir de una pregunta de dominio recién creada (siempre en DRAFT, sin tema ni competencias todavía). */
    public static QuestionRecord toNewRecord(Question question, UserRecord author) {
        if (question instanceof MultipleChoiceQuestion mcq) {
            MultipleChoiceQuestionRecord record = new MultipleChoiceQuestionRecord(mcq.getStatement(), question.getStatus(), author);
            for (AnswerOption opcion : mcq.getOptions()) {
                record.addOption(new AnswerOptionRecord(opcion.getText(), opcion.isCorrect()));
            }
            return record;
        }
        if (question instanceof TrueFalseQuestion tfq) {
            return new TrueFalseQuestionRecord(tfq.getStatement(), question.getStatus(), author, tfq.getExpectedAnswer());
        }
        throw new IllegalStateException("Subtipo de pregunta no reconocido: " + question.getClass());
    }

    public static QuestionMetadataRecord toRecordMetadata(QuestionMetadata metadata) {
        if (metadata == null) return null;
        return new QuestionMetadataRecord(metadata.getCognitiveLevel(), metadata.getEstimatedTimeMinutes(), metadata.getNotes(), metadata.getLastReviewedAt());
    }

    private static QuestionMetadata toDomainMetadata(QuestionMetadataRecord record) {
        if (record == null) return null;
        return new QuestionMetadata(record.getCognitiveLevel(), record.getEstimatedTimeMinutes(), record.getNotes(), record.getLastReviewedAt());
    }
}
