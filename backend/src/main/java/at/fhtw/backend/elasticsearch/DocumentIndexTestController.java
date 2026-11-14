
package at.fhtw.backend.elasticsearch;

import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

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

    @GetMapping("/search")
    public List<DocumentIndex> search(
            @RequestParam String q) throws IOException {

        return searchService.searchDocuments(q);
    }

}
