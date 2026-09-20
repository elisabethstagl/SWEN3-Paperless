package at.fhtw.backend.business;

import at.fhtw.backend.model.Document;
import at.fhtw.backend.persistence.DocumentRepository;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentByID(UUID id) {
        return documentRepository.findById(id);
    }


    public Document upload(MultipartFile file) throws IOException {
        Document doc = extractMetadata(file);
        doc.setId(UUID.randomUUID());

        //save in postgres
        doc = documentRepository.save(doc);

        //save in MINIO


        return doc;
    }


    public Optional<Document> deleteDocument(UUID id) {
        Optional<Document> doc = getDocumentByID(id);
        if (doc.isPresent()) {
            documentRepository.delete(doc.get());

            //delete in MinIO
        }
        return doc;
    }

    public Optional<Document> updateDocument(UUID id, MultipartFile file) throws IOException {
        Optional<Document> doc = getDocumentByID(id);
        if (doc.isPresent()) {
            Document newDoc = extractMetadata(file);
            newDoc.setId(doc.get().getId());

            //save in MinIO

            return Optional.of(documentRepository.save(newDoc));
        }
        return doc;
    }


    private Document extractMetadata(MultipartFile file) throws IOException {

        //read PDF metadata in iTextPDF
        PdfReader reader = new PdfReader(file.getInputStream());
        PdfDocument doc = new PdfDocument(reader);
        PdfDocumentInfo docInfo = doc.getDocumentInfo();

        //get created on Date
        String rawCreationDate = doc.getDocumentInfo().getMoreInfo("CreationDate");
        Calendar calender = PdfDate.decode(rawCreationDate);
        Date createdOn = calender.getTime();
        System.out.println("Raw Creation Date: " + createdOn);


        return Document.builder()
                .filename(file.getOriginalFilename())
                .author(docInfo.getAuthor())
                .creator(docInfo.getCreator())
                .pages(doc.getNumberOfPages())
                .createdOn(createdOn)
                .build();
    }
}