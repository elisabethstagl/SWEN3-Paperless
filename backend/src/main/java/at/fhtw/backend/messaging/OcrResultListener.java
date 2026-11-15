package at.fhtw.backend.messaging;

import at.fhtw.backend.business.DocumentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OcrResultListener {

    private final DocumentService documentService;
    private final JsonMapper jsonMapper;

    public OcrResultListener(DocumentService documentService, JsonMapper jsonMapper) {
        this.documentService = documentService;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "${paperless.rabbitmq.ocr-result-queue}")
    public void onOcrResult(byte[] body) {
        OcrResultMessage message = jsonMapper.readValue(body, OcrResultMessage.class);
        documentService.saveContent(message.documentId(), message.text());
    }

}
