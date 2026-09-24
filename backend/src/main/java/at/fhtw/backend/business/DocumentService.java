package at.fhtw.backend.business;

import at.fhtw.backend.PDFMetadataExtractor;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.model.DocumentDTO;
import at.fhtw.backend.model.DocumentMapper;
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

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentByID(UUID id) {
        return documentRepository.findById(id);
    }

    public Optional<DocumentDTO> upload(MultipartFile file) throws IOException {

        UUID id = UUID.randomUUID();

        DocumentDTO dto = PDFMetadataExtractor.extractMetadata(file);

        Document document = documentMapper.toEntity(dto);

        document.setId(id);


        document = documentRepository.save(document);



        return Optional.of(documentMapper.toDTO(document));
    }


    public Optional<DocumentDTO> deleteDocument(UUID id) {
        Optional<Document> doc = getDocumentByID(id);
        if (doc.isEmpty()) {
            return Optional.empty();
        }

        documentRepository.delete(doc.get());
        return Optional.of(documentMapper.toDTO(doc.get()));
    }

    public Optional<DocumentDTO> updateDocument(UUID id, MultipartFile file) throws IOException {
        Optional<Document> existing = getDocumentByID(id);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        DocumentDTO dto = PDFMetadataExtractor.extractMetadata(file);
        documentMapper.updateEntity(dto, existing.get());

        Document saved = documentRepository.save(existing.get());

        return Optional.of(documentMapper.toDTO(saved));


    }


}