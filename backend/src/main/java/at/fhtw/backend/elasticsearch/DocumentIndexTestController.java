
package at.fhtw.backend.elasticsearch;

import at.fhtw.backend.business.DocumentService;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elasticsearch")
public class DocumentIndexTestController {

    private final DocumentSearchService searchService;
    private final DocumentService documentService;

    public DocumentIndexTestController(
            DocumentSearchService searchService,
            DocumentService documentService) {

        this.searchService = searchService;
        this.documentService = documentService;
    }


    @PostMapping("/index-test")
    public String indexTest() throws IOException {

        DocumentIndex document = new DocumentIndex(
                "test-1",
                "SWEN3 Testdokument",
                "Dieses Dokument enthält einen Elasticsearch Volltextsuche Test.",
                "test.pdf",
                "2026-11-13"
        );

        searchService.indexDocument(document);

        return "Document indexed successfully!";
    }

    @GetMapping("/search")
    public List<DocumentIndex> search(
            @RequestParam String q) throws IOException {

        return searchService.searchDocuments(q);
    }


    @PostMapping("/reindex")
    public Map<String, Object> reindex() {

        int count = documentService.reindexAllDocuments();

        return Map.of(
                "message", "Reindexing completed",
                "indexedDocuments", count
        );
    }


}
