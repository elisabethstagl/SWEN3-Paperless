package at.fhtw.backend.presentation;

import at.fhtw.backend.business.DocumentService;
import at.fhtw.backend.business.NoteService;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.dto.DocumentDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService, NoteService noteService) {
        this.documentService = documentService;}

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentByID(@PathVariable UUID id) {
        Optional<Document> created = documentService.getDocumentByID(id);
        if (created.isPresent()) {
            return new ResponseEntity<>(created.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<DocumentDTO> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        Optional<DocumentDTO> created = documentService.upload(file);
        return new ResponseEntity<>(created.get(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        Optional<DocumentDTO> doc = documentService.updateDocument(id, file);

        if (doc.isPresent()) {
            return new ResponseEntity<>(doc.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DocumentDTO> deleteDocument(@PathVariable UUID id) {
        Optional<DocumentDTO> doc = documentService.deleteDocument(id);

        if (doc.isPresent()) {
            return new ResponseEntity<>(doc.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

