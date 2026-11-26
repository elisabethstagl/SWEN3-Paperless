package at.fhtw.backend.messaging;

import at.fhtw.backend.business.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class GenAiResultListener {

    private final DocumentService documentService;
    private final JsonMapper jsonMapper;

    public GenAiResultListener(
            DocumentService documentService,
            JsonMapper jsonMapper
    ) {
        this.documentService = documentService;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(
            queues = "${paperless.rabbitmq.genai-result-queue}"
    )
    public void onGenAiResult(byte[] body) {

        GenAiResultMessage message;

        try {
            message = jsonMapper.readValue(body, GenAiResultMessage.class);

            if (message == null) {
                throw new IllegalArgumentException("Empty message");
            }

        } catch (Exception e) {
            log.error("Invalid GenAI result message", e);

            throw new AmqpRejectAndDontRequeueException(
                    "Invalid GenAI result message", e
            );
        }

        log.info("Received GenAI result for document {}", message.documentId());

        documentService.saveSummary(message.documentId(), message.summary());
    }
}