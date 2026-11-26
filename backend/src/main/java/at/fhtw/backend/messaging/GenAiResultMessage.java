package at.fhtw.backend.messaging;

import java.util.UUID;

public record GenAiResultMessage(
        UUID documentId,
        String summary
) {
}