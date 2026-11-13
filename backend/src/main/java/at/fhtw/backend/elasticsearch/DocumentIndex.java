package at.fhtw.backend.elasticsearch;

public record DocumentIndex(
        String id,
        String title,
        String content,
        String fileName,
        String createdAt) {

}
