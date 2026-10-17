package at.fhtw.backend.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DocumentMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String ocrRequestQueue;
    private final String genAiRequestQueue;

    public DocumentMessageProducer(RabbitTemplate rabbitTemplate,
                                   @Value("${paperless.rabbitmq.ocr-request-queue}") String ocrRequestQueue,
                                   @Value("${paperless.rabbitmq.genai-request-queue}") String genAiRequestQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.ocrRequestQueue = ocrRequestQueue;
        this.genAiRequestQueue = genAiRequestQueue;
    }

    public void sendOcrRequest(UUID documentId) {
        rabbitTemplate.convertAndSend(ocrRequestQueue, documentId.toString());
    }

}
