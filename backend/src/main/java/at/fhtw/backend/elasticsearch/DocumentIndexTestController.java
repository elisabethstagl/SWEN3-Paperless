
package at.fhtw.backend.elasticsearch;

import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/elasticsearch")
public class DocumentIndexTestController {

    private final DocumentSearchService searchService;

    public DocumentIndexTestController(
            DocumentSearchService searchService) {
        this.searchService = searchService;
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
}
