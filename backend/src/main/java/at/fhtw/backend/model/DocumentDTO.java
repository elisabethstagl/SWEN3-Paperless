package at.fhtw.backend.model;



import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class DocumentDTO {
    UUID id;

    String filename;

    String author;

    String creator;

    Integer pages;

    Date createdOn;

    String summary;

    List<Note> notes;

}
