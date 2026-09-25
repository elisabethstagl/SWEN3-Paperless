package at.fhtw.backend.presentation;

import at.fhtw.backend.business.NoteService;
import at.fhtw.backend.model.Note;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/documents/{documentId}/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@PathVariable UUID documentId) {

        return ResponseEntity.ok(noteService.getAllNotes(documentId));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteById(
            @PathVariable UUID documentId,
            @PathVariable UUID noteId) {

        Optional<Note> note =
                noteService.getNoteById(documentId, noteId);

        if (note.isPresent()) {
            return new ResponseEntity<>(note.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@PathVariable UUID documentId, @RequestBody Note note) {

        Optional<Note> createdNote = noteService.addNote(
                documentId,
                note.getAuthor(),
                note.getContent()
        );

        if (createdNote.isPresent()) {
            return new ResponseEntity<>(createdNote.get(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable UUID documentId, @PathVariable UUID noteId, @RequestBody Note note) {

        Optional<Note> updatedNote = noteService.updateNote(
                documentId,
                noteId,
                note.getAuthor(),
                note.getContent()
        );

        if (updatedNote.isPresent()) {
            return new ResponseEntity<>(updatedNote.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable UUID documentId, @PathVariable UUID noteId) {

        boolean deleted = noteService.deleteNote(documentId, noteId);

        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


