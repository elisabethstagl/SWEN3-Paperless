package at.fhtw.backend.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue ocrRequestQueue(@Value("${paperless.rabbitmq.ocr-request-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue ocrResultQueue(@Value("${paperless.rabbitmq.ocr-result-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue genAiRequestQueue(@Value("${paperless.rabbitmq.genai-request-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue genAiResultQueue(@Value("${paperless.rabbitmq.genai-result-queue}") String queueName) {
        return new Queue(queueName, true);
    }
}
