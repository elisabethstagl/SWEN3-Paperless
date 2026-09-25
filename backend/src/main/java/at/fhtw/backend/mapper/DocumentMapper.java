package at.fhtw.backend.mapper;

import at.fhtw.backend.model.Document;
import at.fhtw.backend.dto.DocumentDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = NoteMapper.class)
public interface DocumentMapper {
    Document toEntity(DocumentDTO dto);
    DocumentDTO toDTO(Document doc);

    @Mapping(target = "id", ignore = true)
    void updateEntity(DocumentDTO dto, @MappingTarget Document doc);

}
