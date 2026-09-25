package at.fhtw.backend.business;

import at.fhtw.backend.model.Document;
import at.fhtw.backend.mapper.DocumentMapper;
import at.fhtw.backend.model.Note;
import at.fhtw.backend.persistence.DocumentRepository;
import at.fhtw.backend.persistence.NoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class NoteService {

    private final DocumentRepository documentRepository;
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository, DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.noteRepository = noteRepository;
        this.documentRepository = documentRepository;
    }


    public List<Note> getAllNotes(UUID documentId) {
        return noteRepository.findByDocumentId(documentId);
    }

    public Optional<Note> getNoteById(UUID documentId, UUID noteId) {

        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty()) {
            return Optional.empty();
        }

        if (!note.get().getDocument().getId().equals(documentId)) {
            return Optional.empty();
        }

        return note;
    }

    public Optional<Note> addNote(
            UUID documentId,
            String author,
            String content) {

        Optional<Document> doc = documentRepository.findById(documentId);

        if (doc.isEmpty()) {
            return Optional.empty();
        }

        Note newNote = Note.builder()
                .id(UUID.randomUUID())
                .author(author)
                .content(content)
                .document(doc.get())
                .build();

        Note savedNote = noteRepository.save(newNote);

        return Optional.of(savedNote);
    }

    public Optional<Note> updateNote(
            UUID documentId,
            UUID noteId,
            String author,
            String content) {

        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty()) {
            return Optional.empty();
        }

        if (!note.get().getDocument().getId().equals(documentId)) {
            return Optional.empty();
        }

        note.get().setAuthor(author);
        note.get().setContent(content);

        return note;
    }

    public boolean deleteNote(UUID documentId, UUID noteId) {
        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty()) {
            return false;
        }

        if (!note.get().getDocument().getId().equals(documentId)) {
            return false;
        }

        noteRepository.delete(note.get());
        return true;
    }

}
