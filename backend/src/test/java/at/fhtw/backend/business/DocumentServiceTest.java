package at.fhtw.backend.business;

import at.fhtw.backend.model.Document;
import at.fhtw.backend.dto.DocumentDTO;
import at.fhtw.backend.persistence.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void getAllDocuments_returnsAllDocuments() {
        Document document1 = Document.builder()
                .id(UUID.randomUUID())
                .filename("first.pdf")
                .pages(5)
                .build();

        Document document2 = Document.builder()
                .id(UUID.randomUUID())
                .filename("second.pdf")
                .pages(10)
                .build();

        List<Document> documents = List.of(document1, document2);

        when(documentRepository.findAll()).thenReturn(documents);

        List<Document> result = documentService.getAllDocuments();

        assertEquals(2, result.size());
        assertEquals("first.pdf", result.get(0).getFilename());
        assertEquals("second.pdf", result.get(1).getFilename());
    }

    @Test
    void getDocumentById_existingDocument_returnsDocument() {
        UUID id = UUID.randomUUID();

        Document document = Document.builder()
                .id(id)
                .filename("test.pdf")
                .pages(3)
                .build();

        when(documentRepository.findById(id))
                .thenReturn(Optional.of(document));

        Optional<Document> result = documentService.getDocumentByID(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("test.pdf", result.get().getFilename());
    }

    @Test
    void getDocumentById_unknownDocument_returnsEmpty() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(documentRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act
        Optional<Document> result = documentService.getDocumentByID(id);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteDocument_existingDocument_deletesDocument() {
        UUID id = UUID.randomUUID();

        Document document = Document.builder()
                .id(id)
                .filename("test.pdf")
                .build();

        when(documentRepository.findById(id))
                .thenReturn(Optional.of(document));

        Optional<DocumentDTO> result = documentService.deleteDocument(id);

        assertTrue(result.isPresent());
        assertEquals(document, result.get());

        //verify - was the method actually called from the service (documentRepository.delete(document))
        verify(documentRepository).delete(document);
    }

    @Test
    void deleteDocument_unknownDocument_doesNotDeleteAnything() {
        UUID id = UUID.randomUUID();

        when(documentRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<DocumentDTO> result = documentService.deleteDocument(id);

        assertTrue(result.isEmpty());
        verify(documentRepository, never()).delete(any());
    }
}