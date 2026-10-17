package at.fhtw.backend.business;

import at.fhtw.backend.dto.NoteDTO;
import at.fhtw.backend.mapper.NoteMapper;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.model.Note;
import at.fhtw.backend.persistence.DocumentRepository;
import at.fhtw.backend.persistence.NoteRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class NoteService {

    private final DocumentRepository documentRepository;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, DocumentRepository documentRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.documentRepository = documentRepository;
        this.noteMapper = noteMapper;
    }


    public List<NoteDTO> getAllNotes(UUID documentId) {

        List<Note> notes = noteRepository.findByDocumentId(documentId);
        log.debug("Fetched {} notes for document {}", notes.size(), documentId);
        List<NoteDTO> noteDTOs = new ArrayList<>();

        for (Note note : notes) {
            NoteDTO noteDTO = noteMapper.toDTO(note);
            noteDTOs.add(noteDTO);
        }

        return noteDTOs;
    }

    public Optional<NoteDTO> getNoteById(UUID documentId, UUID noteId) {

        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty()) {
            return Optional.empty();
        }

        if (!note.get().getDocument().getId().equals(documentId)) {
            return Optional.empty();
        }

        return Optional.of(noteMapper.toDTO(note.get()));
    }

    public Optional<NoteDTO> addNote(
            UUID documentId,
            NoteDTO noteDTO) {

        Optional<Document> doc =
                documentRepository.findById(documentId);

        if (doc.isEmpty()) {
            log.warn("Add note failed: document {} not found", documentId);
            return Optional.empty();
        }

        Note newNote = noteMapper.toEntity(noteDTO);

        newNote.setId(UUID.randomUUID());
        newNote.setDocument(doc.get());

        Note savedNote = noteRepository.save(newNote);
        log.info("Note {} added to document {}", savedNote.getId(), documentId);

        return Optional.of(noteMapper.toDTO(savedNote));
    }

    public Optional<NoteDTO> updateNote(
            UUID documentId,
            UUID noteId,
            NoteDTO noteDTO) {

        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty() || !note.get().getDocument().getId().equals(documentId)) {
            log.warn("Update failed: note {} not found for document {}", noteId, documentId);
            return Optional.empty();
        }

        noteMapper.updateEntity(noteDTO, note.get());

        Note savedNote = noteRepository.save(note.get());
        log.info("Note {} updated", noteId);

        return Optional.of(noteMapper.toDTO(savedNote));
    }

    public boolean deleteNote(UUID documentId, UUID noteId) {
        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty() || !note.get().getDocument().getId().equals(documentId)) {
            log.warn("Delete failed: note {} not found for document {}", noteId, documentId);
            return false;
        }

        noteRepository.delete(note.get());
        log.info("Note {} deleted", noteId);
        return true;
    }

}
