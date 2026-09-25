package at.fhtw.backend.presentation;

import at.fhtw.backend.business.NoteService;
import at.fhtw.backend.dto.NoteDTO;
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
    public ResponseEntity<List<NoteDTO>> getAllNotes(@PathVariable UUID documentId) {

        return ResponseEntity.ok(noteService.getAllNotes(documentId));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDTO> getNoteById(
            @PathVariable UUID documentId,
            @PathVariable UUID noteId) {

        Optional<NoteDTO> note =
                noteService.getNoteById(documentId, noteId);

        if (note.isPresent()) {
            return new ResponseEntity<>(note.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<NoteDTO> addNote(@PathVariable UUID documentId, @RequestBody NoteDTO note) {

        Optional<NoteDTO> createdNote = noteService.addNote(
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
    public ResponseEntity<NoteDTO> updateNote(@PathVariable UUID documentId, @PathVariable UUID noteId, @RequestBody NoteDTO note) {

        Optional<NoteDTO> updatedNote = noteService.updateNote(
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


