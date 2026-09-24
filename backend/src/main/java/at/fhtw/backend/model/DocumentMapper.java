package at.fhtw.backend.model;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    Document toEntity(DocumentDTO dto);
    DocumentDTO toDTO(Document doc);

    @Mapping(target = "id", ignore = true)
    void updateEntity(DocumentDTO dto, @MappingTarget Document doc);

}
