
package at.fhtw.backend.elasticsearch;

import at.fhtw.backend.business.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elasticsearch")
public class DocumentSearchController {

    private final DocumentSearchService searchService;
    private final DocumentService documentService;

    public DocumentSearchController(
            DocumentSearchService searchService,
            DocumentService documentService) {

        this.searchService = searchService;
        this.documentService = documentService;
    }

    @GetMapping("/search")
    public List<DocumentIndex> search(
            @RequestParam String q) throws IOException {

        return searchService.searchDocuments(q);
    }

    @GetMapping("/documents/{id}/search")
    public DocumentHighlightResult searchInDocument(
            @PathVariable String id,
            @RequestParam String q) throws IOException {

        return searchService.searchInDocument(id, q);
    }

    @PostMapping("/reindex")
    public Map<String, Object> reindex() {

        int count = documentService.reindexAllDocuments();

        return Map.of(
                "message", "Reindexing completed",
                "queuedDocuments", count
        );
    }
}
