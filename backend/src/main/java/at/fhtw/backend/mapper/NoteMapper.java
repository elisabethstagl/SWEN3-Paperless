package at.fhtw.backend.mapper;

import at.fhtw.backend.dto.NoteDTO;
import at.fhtw.backend.model.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "document", ignore = true)
    @Mapping(target = "id", ignore = true)
    Note toEntity(NoteDTO noteDTO);

    NoteDTO toDTO(Note note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "document", ignore = true)
    void updateEntity(NoteDTO noteDTO, @MappingTarget Note note);
}
