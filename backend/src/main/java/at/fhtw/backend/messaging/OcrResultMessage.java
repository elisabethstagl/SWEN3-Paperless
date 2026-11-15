package at.fhtw.backend.messaging;

import java.util.UUID;

public record OcrResultMessage(UUID documentId, String text) {
}
