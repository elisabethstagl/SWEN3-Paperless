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

    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream().map(doc -> documentMapper.toDTO(doc)).toList();
    }

    public Optional<DocumentDTO> getDocumentByID(UUID id) {
        return Optional.of(documentMapper.toDTO(documentRepository.getDocumentById((id))));
    }

    public Optional<DocumentDTO> upload(MultipartFile file) throws IOException {

        UUID id = UUID.randomUUID();

        DocumentDTO dto = PDFMetadataExtractor.extractMetadata(file);

        Document document = documentMapper.toEntity(dto);

        document.setId(id);


        document = documentRepository.save(document);



        return Optional.of(documentMapper.toDTO(document));
    }


    public void deleteDocument(UUID id) {
/*
        Document doc = documentRepository.getDocumentById(id)
*/
        Document doc = documentRepository.findById(id).orElseThrow(() -> new Exception("Document not found"));
        documentRepository.delete(doc);
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