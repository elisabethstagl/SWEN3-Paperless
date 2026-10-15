package at.fhtw.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    UUID id;

    @NotBlank(message = "Filename must not be empty")
    String filename;

    String author;

    String creator;

    @NotNull(message = "Page count must not be null")
    @Min(value = 1, message = "Document must have at least one page")
    Integer pages;

    Date createdOn;

    String summary;

    List<NoteDTO> notes;

}
