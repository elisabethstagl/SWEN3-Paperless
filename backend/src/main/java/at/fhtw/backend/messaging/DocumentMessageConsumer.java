package at.fhtw.backend.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DocumentMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentMessageConsumer.class);


    @RabbitListener(queues = "${paperless.rabbitmq.ocr-request-queue}")
    public void receiveOcrResult(String documentId) {
        log.info("received ocr request for document {}", documentId);
    }
}
