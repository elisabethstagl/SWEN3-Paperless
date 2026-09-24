package at.fhtw.backend.business;

import at.fhtw.backend.model.Document;
import at.fhtw.backend.model.DocumentDTO;
import at.fhtw.backend.model.DocumentMapper;
import at.fhtw.backend.model.Note;
import at.fhtw.backend.persistence.DocumentRepository;
import at.fhtw.backend.persistence.NoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NoteService {

    private final DocumentRepository documentRepository;
    private final NoteRepository noteRepository;
    private final DocumentMapper documentMapper;

    public NoteService(NoteRepository noteRepository, DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.noteRepository = noteRepository;
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }


    public Optional<DocumentDTO> addNote(UUID documentID, String note) {
        Optional<Document> doc = documentRepository.findById(documentID);
        if (doc.isPresent()) {
            Note newNote = Note.builder().content(note).author("author").id(UUID.randomUUID()).build();
            newNote.setDocument(doc.get());
            noteRepository.save(newNote);
        }
        return Optional.of(documentMapper.toDTO(doc.get()));
    }

}
