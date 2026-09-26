package at.fhtw.backend.business;

import at.fhtw.backend.mapper.DocumentMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

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

        DocumentDTO dto1 = DocumentDTO.builder()
                .id(document1.getId())
                .filename("first.pdf")
                .pages(5)
                .build();

        DocumentDTO dto2 = DocumentDTO.builder()
                .id(document2.getId())
                .filename("second.pdf")
                .pages(10)
                .build();

        when(documentRepository.findAll())
                .thenReturn(List.of(document1, document2));

        when(documentMapper.toDTO(document1)).thenReturn(dto1);
        when(documentMapper.toDTO(document2)).thenReturn(dto2);

        List<DocumentDTO> result = documentService.getAllDocuments();

        assertEquals(2, result.size());
        assertEquals("first.pdf", result.get(0).getFilename());
        assertEquals("second.pdf", result.get(1).getFilename());

        verify(documentRepository).findAll();
        verify(documentMapper).toDTO(document1);
        verify(documentMapper).toDTO(document2);
    }

    @Test
    void getDocumentById_existingDocument_returnsDocumentDTO() {
        UUID id = UUID.randomUUID();

        Document document = Document.builder()
                .id(id)
                .filename("test.pdf")
                .pages(3)
                .build();

        DocumentDTO dto = DocumentDTO.builder()
                .id(id)
                .filename("test.pdf")
                .pages(3)
                .build();

        when(documentRepository.findById(id))
                .thenReturn(Optional.of(document));

        when(documentMapper.toDTO(document))
                .thenReturn(dto);

        Optional<DocumentDTO> result = documentService.getDocumentByID(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("test.pdf", result.get().getFilename());
        assertEquals(3, result.get().getPages());

        verify(documentRepository).findById(id);
        verify(documentMapper).toDTO(document);
    }

    @Test
    void getDocumentById_unknownDocument_returnsEmpty() {
        UUID id = UUID.randomUUID();

        when(documentRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<DocumentDTO> result = documentService.getDocumentByID(id);

        assertTrue(result.isEmpty());

        verify(documentRepository).findById(id);
        verify(documentMapper, never()).toDTO(any());
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

        boolean result = documentService.deleteDocument(id);

        assertTrue(result);

        verify(documentRepository).findById(id);
        verify(documentRepository).delete(document);
    }

    @Test
    void deleteDocument_unknownDocument_doesNotDeleteAnything() {
        UUID id = UUID.randomUUID();

        when(documentRepository.findById(id))
                .thenReturn(Optional.empty());

        boolean result = documentService.deleteDocument(id);

        assertFalse(result);

        verify(documentRepository).findById(id);
        verify(documentRepository, never()).delete(any());
    }
}