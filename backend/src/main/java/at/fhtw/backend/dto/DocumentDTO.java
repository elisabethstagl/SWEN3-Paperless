package at.fhtw.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    String filename;

    @Size(max = 100, message = "Author must not exceed 100 characters")
    String author;

    @Size(max = 100, message = "Creator must not exceed 100 characters")
    String creator;

    @NotNull(message = "Page count must not be null")
    @Min(value = 1, message = "Document must have at least one page")
    Integer pages;

    Date createdOn;

    @Size(max = 2000, message = "Summary must not exceed 2000 characters")
    String summary;

    List<NoteDTO> notes;

}
