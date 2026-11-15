
package at.fhtw.backend.messaging;

import at.fhtw.backend.elasticsearch.DocumentIndex;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.UUID;

@Component
public class DocumentMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String ocrRequestQueue;
    private final String genAiRequestQueue;
    private final JsonMapper jsonMapper;
    private final String indexingQueue;

    public DocumentMessageProducer(
            RabbitTemplate rabbitTemplate,
            JsonMapper jsonMapper,
            @Value("${paperless.rabbitmq.ocr-request-queue}")
            String ocrRequestQueue,
            @Value("${paperless.rabbitmq.genai-request-queue}")
            String genAiRequestQueue,
            @Value("${paperless.rabbitmq.indexing-queue}")
            String indexingQueue
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.ocrRequestQueue = ocrRequestQueue;
        this.genAiRequestQueue = genAiRequestQueue;
        this.jsonMapper = jsonMapper;
        this.indexingQueue = indexingQueue;
    }

    public void sendOcrRequest(UUID documentId) {
        rabbitTemplate.convertAndSend(
                ocrRequestQueue,
                documentId.toString()
        );
    }

    public void sendIndexingRequest(DocumentIndex document) {
        Map<String, Object> message = Map.of(
                "operation", "index",
                "id", document.id(),
                "title", document.title(),
                "content", document.content(),
                "fileName", document.fileName(),
                "createdAt", document.createdAt()
        );

        sendIndexingMessage(message);
    }

    public void sendDeleteRequest(UUID documentId) {
        sendIndexingMessage(Map.of(
                "operation", "delete",
                "id", documentId.toString()
        ));
    }

    private void sendIndexingMessage(Map<String, Object> message) {
        byte[] json = jsonMapper.writeValueAsBytes(message);

        rabbitTemplate.convertAndSend(
                indexingQueue,
                json,
                rabbitMessage -> {
                    rabbitMessage.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    rabbitMessage.getMessageProperties()
                            .setContentType("application/json");

                    return rabbitMessage;
                }
        );
    }
}
