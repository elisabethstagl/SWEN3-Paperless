package at.fhtw.backend;

import at.fhtw.backend.dto.DocumentDTO;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PDFMetadataExtractor {

    public static DocumentDTO extractMetadata(MultipartFile file) throws IOException {

        try (PdfReader reader = new PdfReader(file.getInputStream());
             PdfDocument doc = new PdfDocument(reader)) {

            // Read PDF metadata
            PdfDocumentInfo docInfo = doc.getDocumentInfo();

            // Get creation date (might be missing)
            String rawCreationDate = docInfo.getMoreInfo("CreationDate");
            Calendar calendar = PdfDate.decode(rawCreationDate);

            Date createdOn = null;

            if (calendar != null) {
                createdOn = calendar.getTime();
            }

            return DocumentDTO.builder()
                    .filename(file.getOriginalFilename())
                    .author(docInfo.getAuthor())
                    .creator(docInfo.getCreator())
                    .pages(doc.getNumberOfPages())
                    .createdOn(createdOn)
                    .build();
        }
    }
}