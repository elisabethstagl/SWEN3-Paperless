package at.fhtw.backend.business;

import at.fhtw.backend.PDFMetadataExtractor;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.dto.DocumentDTO;
import at.fhtw.backend.mapper.DocumentMapper;
import at.fhtw.backend.model.Note;
import at.fhtw.backend.persistence.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    public List<DocumentDTO> getAllDocuments() {

        List<Document> documents = documentRepository.findAll();
        List<DocumentDTO> documentDTOs = new ArrayList<>();

        for (Document document : documents) {
            DocumentDTO documentDTO = documentMapper.toDTO(document);
            documentDTOs.add(documentDTO);
        }

        return documentDTOs;
    }

    public Optional<DocumentDTO> getDocumentByID(UUID id) {
        Optional<Document> document = documentRepository.findById(id);

        if (document.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(documentMapper.toDTO(document.get()));
    }

    public Optional<DocumentDTO> upload(MultipartFile file) throws IOException {

        UUID id = UUID.randomUUID();

        DocumentDTO dto = PDFMetadataExtractor.extractMetadata(file);

        Document document = documentMapper.toEntity(dto);

        document.setId(id);


        document = documentRepository.save(document);


        return Optional.of(documentMapper.toDTO(document));
    }

    public boolean deleteDocument(UUID id) {
        Optional<Document> doc = documentRepository.findById(id);

        if (doc.isEmpty()) {
            return false;
        }

        documentRepository.delete(doc.get());
        return true;
    }

    public Optional<DocumentDTO> updateDocument(UUID id, MultipartFile file) throws IOException {

        Optional<Document> existing = documentRepository.findById(id);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        DocumentDTO dto = PDFMetadataExtractor.extractMetadata(file);

        documentMapper.updateEntity(dto, existing.get());

        Document saved = documentRepository.save(existing.get());

        return Optional.of(documentMapper.toDTO(saved));
    }

}