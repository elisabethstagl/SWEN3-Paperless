package at.fhtw.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder

public class NoteDTO {

    UUID id;
    String author;
    String content;
}
