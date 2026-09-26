package at.fhtw.backend.mapper;

import at.fhtw.backend.dto.NoteDTO;
import at.fhtw.backend.model.Document;
import at.fhtw.backend.model.Note;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteMapperTest {

    private final NoteMapper noteMapper = Mappers.getMapper(NoteMapper.class);

    @Test
    void toDTO_mapsNoteToDTO() {
        UUID id = UUID.randomUUID();

        Note note = Note.builder()
                .id(id)
                .content("this is a test content - Entity to DTO")
                .author("test author")
                .build();

        NoteDTO result = noteMapper.toDTO(note);

        assertEquals(id, result.getId());
        assertEquals("this is a test content - Entity to DTO", result.getContent());
        assertEquals("test author", result.getAuthor());
    }

    @Test
    void toEntity_mapsDTOToNote() {
        UUID id = UUID.randomUUID();

        NoteDTO noteDTO = NoteDTO.builder()
                .id(id)
                .content("this is a test content - DTO to Entity")
                .author("test author")
                .build();

        Note result = noteMapper.toEntity(noteDTO);

        assertNull(result.getId());
        assertNull(result.getDocument());
        assertEquals("this is a test content - DTO to Entity", result.getContent());
        assertEquals("test author", result.getAuthor());
    }

    @Test
    void updateEntity_updatesFieldsButKeepsIdAndDocument() {
        UUID noteId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

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
                .id(UUID.randomUUID())
                .author("New Author")
                .content("New Content")
                .build();

        noteMapper.updateEntity(updateDTO, note);

        assertEquals("New Author", note.getAuthor());
        assertEquals("New Content", note.getContent());

        assertEquals(noteId, note.getId());
        assertEquals(document, note.getDocument());
    }
}