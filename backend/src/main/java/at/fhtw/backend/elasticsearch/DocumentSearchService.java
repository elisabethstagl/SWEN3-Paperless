
package at.fhtw.backend.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.util.NamedValue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DocumentSearchService {

    private final ElasticsearchClient client;

    public DocumentSearchService(ElasticsearchClient client) {
        this.client = client;
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

    public DocumentHighlightResult searchInDocument(
            String documentId,
            String query) throws IOException {

        SearchResponse<DocumentIndex> response = client.search(s -> s
                        .index("paperless-documents")
                        .size(1)
                        .query(q -> q.bool(b -> b
                                .filter(f -> f.term(t -> t
                                        .field("_id")
                                        .value(documentId)
                                ))
                                .must(m -> m.match(mm -> mm
                                        .field("content")
                                        .query(query)
                                ))
                        ))

                        .highlight(h -> h
                                .preTags("[[HIT]]")
                                .postTags("[[/HIT]]")
                                .fields(List.of(
                                        NamedValue.of(
                                                "content",
                                                HighlightField.of(f -> f
                                                        .fragmentSize(160)
                                                        .numberOfFragments(10)
                                                )
                                        )
                                ))
                        ),
                DocumentIndex.class
        );

        List<String> snippets = response.hits().hits()
                .stream()
                .flatMap(hit -> hit.highlight()
                        .getOrDefault("content", List.of())
                        .stream())
                .toList();

        return new DocumentHighlightResult(documentId, snippets);
    }

}
