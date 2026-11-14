
package at.fhtw.backend.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    public List<DocumentIndex> searchDocuments(String query)
            throws IOException {

        SearchResponse<DocumentIndex> response =
                client.search(s -> s
                                .index("paperless-documents")
                                .query(q -> q
                                        .multiMatch(m -> m
                                                .query(query)
                                                .fields("title", "content")
                                        )
                                ),
                        DocumentIndex.class
                );

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .filter(Objects::nonNull)
                .toList();
    }
}
