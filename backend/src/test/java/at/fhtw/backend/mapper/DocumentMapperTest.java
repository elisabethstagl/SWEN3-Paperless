package at.fhtw.backend.mapper;

import at.fhtw.backend.dto.DocumentDTO;
import at.fhtw.backend.model.Document;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentMapperTest {

    private final DocumentMapper documentMapper = Mappers.getMapper(DocumentMapper.class);

    @Test
    void toDTO_mapsDocumentToDTO() {
        UUID id = UUID.randomUUID();

        Document document = Document.builder()
                .id(id)
                .filename("test.pdf")
                .author("John Doe")
                .creator("PDF Creator")
                .pages(10)
                .summary("Some summary")
                .build();

        DocumentDTO result = documentMapper.toDTO(document);

        assertEquals(id, result.getId());
        assertEquals("test.pdf", result.getFilename());
        assertEquals("John Doe", result.getAuthor());
        assertEquals("PDF Creator", result.getCreator());
        assertEquals(10, result.getPages());
        assertEquals("Some summary", result.getSummary());
    }

    @Test
    void toEntity_mapsDTOToDocument() {
        UUID id = UUID.randomUUID();

        DocumentDTO dto = DocumentDTO.builder()
                .id(id)
                .filename("test.pdf")
                .author("John Doe")
                .pages(10)
                .build();

        Document result = documentMapper.toEntity(dto);

        assertEquals(id, result.getId());
        assertEquals("test.pdf", result.getFilename());
        assertEquals("John Doe", result.getAuthor());
        assertEquals(10, result.getPages());
    }
}
