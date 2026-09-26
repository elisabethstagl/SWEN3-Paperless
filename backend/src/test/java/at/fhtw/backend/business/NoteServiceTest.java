package at.fhtw.backend.business;

import at.fhtw.backend.dto.NoteDTO;
import at.fhtw.backend.mapper.NoteMapper;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.model.Note;
import at.fhtw.backend.persistence.DocumentRepository;
import at.fhtw.backend.persistence.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteService noteService;


    @Test
    void getAllNotes_returnsAllNotes() {
        UUID documentId = UUID.randomUUID();

        Note note1 = Note.builder()
                .id(UUID.randomUUID())
                .author("Author 1")
                .content("Content 1")
                .build();

        Note note2 = Note.builder()
                .id(UUID.randomUUID())
                .author("Author 2")
                .content("Content 2")
                .build();

        NoteDTO dto1 = NoteDTO.builder()
                .id(note1.getId())
                .author("Author 1")
                .content("Content 1")
                .build();

        NoteDTO dto2 = NoteDTO.builder()
                .id(note2.getId())
                .author("Author 2")
                .content("Content 2")
                .build();

        when(noteRepository.findByDocumentId(documentId))
                .thenReturn(List.of(note1, note2));

        when(noteMapper.toDTO(note1)).thenReturn(dto1);
        when(noteMapper.toDTO(note2)).thenReturn(dto2);

        List<NoteDTO> result = noteService.getAllNotes(documentId);

        assertEquals(2, result.size());
        assertEquals("Content 1", result.get(0).getContent());
        assertEquals("Content 2", result.get(1).getContent());

        verify(noteRepository).findByDocumentId(documentId);
        verify(noteMapper).toDTO(note1);
        verify(noteMapper).toDTO(note2);
    }


    @Test
    void getNoteById_existingNote_returnsNote() {
        UUID documentId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        Document document = Document.builder()
                .id(documentId)
                .build();

        Note note = Note.builder()
                .id(noteId)
                .author("Test Author")
                .content("Test Content")
                .document(document)
                .build();

        NoteDTO dto = NoteDTO.builder()
                .id(noteId)
                .author("Test Author")
                .content("Test Content")
                .build();

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.of(note));

        when(noteMapper.toDTO(note))
                .thenReturn(dto);

        Optional<NoteDTO> result =
                noteService.getNoteById(documentId, noteId);

        assertTrue(result.isPresent());
        assertEquals(noteId, result.get().getId());
        assertEquals("Test Author", result.get().getAuthor());
        assertEquals("Test Content", result.get().getContent());

        verify(noteRepository).findById(noteId);
        verify(noteMapper).toDTO(note);
    }


    @Test
    void getNoteById_unknownNote_returnsEmpty() {
        UUID documentId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.empty());

        Optional<NoteDTO> result =
                noteService.getNoteById(documentId, noteId);

        assertTrue(result.isEmpty());

        verify(noteRepository).findById(noteId);
        verify(noteMapper, never()).toDTO(any());
    }


    @Test
    void getNoteById_noteBelongsToDifferentDocument_returnsEmpty() {
        UUID requestedDocumentId = UUID.randomUUID();
        UUID actualDocumentId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        Document document = Document.builder()
                .id(actualDocumentId)
                .build();

        Note note = Note.builder()
                .id(noteId)
                .document(document)
                .build();

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.of(note));

        Optional<NoteDTO> result =
                noteService.getNoteById(requestedDocumentId, noteId);

        assertTrue(result.isEmpty());

        verify(noteMapper, never()).toDTO(any());
    }

    @Test
    void addNote_existingDocument_addsNote() {
        UUID documentId = UUID.randomUUID();

        Document document = Document.builder()
                .id(documentId)
                .build();

        NoteDTO inputDTO = NoteDTO.builder()
                .author("Test Author")
                .content("Test Content")
                .build();

        Note newNote = Note.builder()
                .author("Test Author")
                .content("Test Content")
                .build();

        NoteDTO resultDTO = NoteDTO.builder()
                .author("Test Author")
                .content("Test Content")
                .build();

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        when(noteMapper.toEntity(inputDTO))
                .thenReturn(newNote);

        when(noteRepository.save(newNote))
                .thenReturn(newNote);

        when(noteMapper.toDTO(newNote))
                .thenReturn(resultDTO);

        Optional<NoteDTO> result =
                noteService.addNote(documentId, inputDTO);

        assertTrue(result.isPresent());
        assertEquals("Test Author", result.get().getAuthor());
        assertEquals("Test Content", result.get().getContent());

        assertNotNull(newNote.getId());
        assertEquals(document, newNote.getDocument());

        verify(documentRepository).findById(documentId);
        verify(noteMapper).toEntity(inputDTO);
        verify(noteRepository).save(newNote);
        verify(noteMapper).toDTO(newNote);
    }

    @Test
    void addNote_unknownDocument_returnsEmpty() {
        UUID documentId = UUID.randomUUID();

        NoteDTO inputDTO = NoteDTO.builder()
                .author("Test Author")
                .content("Test Content")
                .build();

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.empty());

        Optional<NoteDTO> result =
                noteService.addNote(documentId, inputDTO);

        assertTrue(result.isEmpty());

        verify(documentRepository).findById(documentId);

        verify(noteMapper, never()).toEntity(any());
        verify(noteRepository, never()).save(any());
        verify(noteMapper, never()).toDTO(any());
    }

    @Test
    void updateNote_existingNote_updatesNote() {
        UUID documentId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        Document document = Document.builder()
                .id(documentId)
                .build();

        Note note = Note.builder()
                .id(noteId)
                .author("Old Author")
                .content("Old Content")
                .document(document)
                .build();

        NoteDTO updateDTO = NoteDTO.builder()
                .author("New Author")
                .content("New Content")
                .build();

        NoteDTO resultDTO = NoteDTO.builder()
                .id(noteId)
                .author("New Author")
                .content("New Content")
                .build();

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.of(note));

        when(noteRepository.save(note))
                .thenReturn(note);

        when(noteMapper.toDTO(note))
                .thenReturn(resultDTO);

        Optional<NoteDTO> result =
                noteService.updateNote(
                        documentId,
                        noteId,
                        updateDTO
                );

        assertTrue(result.isPresent());

        assertEquals(noteId, result.get().getId());
        assertEquals("New Author", result.get().getAuthor());
        assertEquals("New Content", result.get().getContent());

        verify(noteRepository).findById(noteId);
        verify(noteMapper).updateEntity(updateDTO, note);
        verify(noteRepository).save(note);
        verify(noteMapper).toDTO(note);
    }

    @Test
    void deleteNote_existingNote_deletesNote() {
        UUID documentId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        Document document = Document.builder()
                .id(documentId)
                .build();

        Note note = Note.builder()
                .id(noteId)
                .document(document)
                .build();

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.of(note));

        boolean result = noteService.deleteNote(documentId, noteId);

        assertTrue(result);

        verify(noteRepository).delete(note);
    }

    @Test
    void deleteNote_unknownNote_doesNotDeleteAnything() {
        UUID documentId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();

        when(noteRepository.findById(noteId))
                .thenReturn(Optional.empty());

        boolean result = noteService.deleteNote(documentId, noteId);

        assertFalse(result);

        verify(noteRepository, never()).delete(any());
    }
}