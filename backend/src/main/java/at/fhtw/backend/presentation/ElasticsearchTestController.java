
package at.fhtw.backend.presentation;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class ElasticsearchTestController {

    private final ElasticsearchClient client;

    public ElasticsearchTestController(
            ElasticsearchClient client
    ) {
        this.client = client;
    }

    @GetMapping("/api/elasticsearch/test")
    public Map<String, Object> testConnection()
            throws IOException {

        var info = client.info();

        return Map.of(
                "connected", true,
                "clusterName", info.clusterName(),
                "version", info.version().number()
        );
    }
}
