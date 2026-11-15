
package at.fhtw.backend.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndexingQueueConfig {

    @Bean
    public Queue indexingQueue(
            @Value("${paperless.rabbitmq.indexing-queue}")
            String queueName
    ) {
        return new Queue(queueName, true);
    }
}
