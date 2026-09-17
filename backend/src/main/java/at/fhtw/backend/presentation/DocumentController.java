package at.fhtw.backend.presentation;

import at.fhtw.backend.business.DocumentService;
import at.fhtw.backend.model.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @PostMapping
    public ResponseEntity<Document> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        Document created = documentService.upload(file);
        return new ResponseEntity<>(created, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentByID(@PathVariable UUID id)  {
        Optional<Document> created = documentService.getDocumentByID(id);
        if(created.isPresent()) {
            return new ResponseEntity<>(created.get(), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        Optional<Document> doc = documentService.updateDocument(id, file);

        if(doc.isPresent()) {
            return new ResponseEntity<>(doc.get(), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Document> deleteDocument(@PathVariable UUID id) {
        Optional<Document> doc = documentService.deleteDocument(id);

        if(doc.isPresent()) {
            return new ResponseEntity<>(doc.get(), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




}
