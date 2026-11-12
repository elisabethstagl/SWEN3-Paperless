package at.fhtw.backend.business;

import at.fhtw.backend.PDFMetadataExtractor;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.dto.DocumentDTO;
import at.fhtw.backend.mapper.DocumentMapper;
import at.fhtw.backend.messaging.DocumentMessageProducer;
import at.fhtw.backend.persistence.DocumentRepository;
import at.fhtw.backend.persistence.MinIORepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final Validator validator;
    private final DocumentMessageProducer documentMessageProducer;
    private final MinIORepository minIORepository;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper, Validator validator,
                           DocumentMessageProducer documentMessageProducer, MinIORepository minIORepository) {

        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
        this.validator = validator;
        this.documentMessageProducer = documentMessageProducer;
        this.minIORepository = minIORepository;
    }

    public List<DocumentDTO> getAllDocuments() {

        List<Document> documents = documentRepository.findAll();
        log.debug("Fetched {} documents", documents.size());
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
            log.warn("Document {} not found", id);
            return Optional.empty();
        }

        return Optional.of(documentMapper.toDTO(document.get()));
    }

    public Optional<InputStream> getDocumentFile(UUID id) {
        if (!documentRepository.existsById(id)) {
            log.warn("File request failed: document {} not found", id);
            return Optional.empty();
        }

        Optional<InputStream> file = Optional.of(minIORepository.load(id));
        if (file.isEmpty()) {
            log.warn("File for document {} not found in storage", id);
        }
        return file;
    }

    public Optional<DocumentDTO> upload(MultipartFile file) throws IOException {

        validateFile(file);

        log.info("Uploading file '{}' ({} bytes)", file.getOriginalFilename(), file.getSize());

        UUID id = UUID.randomUUID();

        DocumentDTO dto = PDFMetadataExtractor.extractMetadata(file);

        // Validate DocumentDTO with Jakarta Bean Validation; checks the object against all validation annotations (in Document DTO)
        Set<ConstraintViolation<DocumentDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            log.warn("Validation failed for '{}': {}", file.getOriginalFilename(), violations);
            throw new ConstraintViolationException(violations);
        }

        Document document = documentMapper.toEntity(dto);

        document.setId(id);

        document = documentRepository.save(document);
        log.info("Document {} saved", document.getId());

        minIORepository.save(file.getInputStream(), id);

        documentMessageProducer.sendOcrRequest(document.getId());

        return Optional.of(documentMapper.toDTO(document));
    }

    public void saveContent(UUID id, String content) {
        Optional<Document> document = documentRepository.findById(id);

        if (document.isEmpty()) {
            log.warn("OCR result discarded: document {} not found", id);
            return;
        }

        minIORepository.saveText(id, content);
        log.info("Content of document {} saved", id);
    }

    public boolean deleteDocument(UUID id) {
        Optional<Document> doc = documentRepository.findById(id);

        if (doc.isEmpty()) {
            log.warn("Delete failed: document {} not found", id);
            return false;
        }

        documentRepository.delete(doc.get());
        minIORepository.delete(id);
        log.info("Document {} deleted", id);
        return true;
    }

    public Optional<DocumentDTO> updateDocument(
            UUID id,
            DocumentDTO documentDTO) {

        Optional<Document> existing = documentRepository.findById(id);

        if (existing.isEmpty()) {
            log.warn("Update failed: document {} not found", id);
            return Optional.empty();
        }

        Set<ConstraintViolation<DocumentDTO>> violations =
                validator.validate(documentDTO);

        if (!violations.isEmpty()) {
            log.warn("Validation failed for document {}: {}", id, violations);
            throw new ConstraintViolationException(violations);
        }

        documentMapper.updateEntity(documentDTO, existing.get());

        Document saved = documentRepository.save(existing.get());
        log.info("Document {} updated", id);

        return Optional.of(documentMapper.toDTO(saved));
    }

    // HELPER

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Upload rejected: file is empty");
            throw new IllegalArgumentException("File must not be empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            log.warn("Upload rejected: '{}' has content type {}", file.getOriginalFilename(), file.getContentType());
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("Upload rejected: file exceeds 10 MB");
            throw new IllegalArgumentException(
                    "File size must not exceed 10 MB"
            );
        }
    }

}
