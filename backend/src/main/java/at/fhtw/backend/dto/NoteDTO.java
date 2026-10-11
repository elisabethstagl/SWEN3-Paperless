package at.fhtw.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    UUID id;

    @NotBlank(message = "Content must not be empty")
    @Size(max = 500, message = "Content must not exceed 500 characters")
    String content;
}
