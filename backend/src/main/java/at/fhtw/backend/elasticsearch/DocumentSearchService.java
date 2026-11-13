
package at.fhtw.backend.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DocumentSearchService {

    private final ElasticsearchClient client;

    public DocumentSearchService(ElasticsearchClient client) {
        this.client = client;
    }

    public void indexDocument(DocumentIndex document)
            throws IOException {

        client.index(i -> i
                .index("paperless-documents")
                .id(document.id())
                .document(document)
        );
    }
}
