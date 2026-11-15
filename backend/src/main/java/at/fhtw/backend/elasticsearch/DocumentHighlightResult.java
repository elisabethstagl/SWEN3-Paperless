package at.fhtw.backend.elasticsearch;

import java.util.List;

public record DocumentHighlightResult(
        String documentId,
        List<String> snippets
) {}
