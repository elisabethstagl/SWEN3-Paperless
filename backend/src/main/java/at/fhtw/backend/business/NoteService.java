package at.fhtw.backend.business;

import at.fhtw.backend.dto.NoteDTO;
import at.fhtw.backend.mapper.NoteMapper;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.model.Note;
import at.fhtw.backend.persistence.DocumentRepository;
import at.fhtw.backend.persistence.NoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            return Optional.empty();
        }

        Note newNote = noteMapper.toEntity(noteDTO);

        newNote.setId(UUID.randomUUID());
        newNote.setDocument(doc.get());

        Note savedNote = noteRepository.save(newNote);

        return Optional.of(noteMapper.toDTO(savedNote));
    }

    public Optional<NoteDTO> updateNote(
            UUID documentId,
            UUID noteId,
            NoteDTO noteDTO) {

        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty()) {
            return Optional.empty();
        }

        if (!note.get().getDocument().getId().equals(documentId)) {
            return Optional.empty();
        }

        noteMapper.updateEntity(noteDTO, note.get());

        Note savedNote = noteRepository.save(note.get());

        return Optional.of(noteMapper.toDTO(savedNote));
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
