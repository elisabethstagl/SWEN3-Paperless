package at.fhtw.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "note")
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @Id
    UUID id;

    @ManyToOne
    @JoinColumn(name = "document_id")
    @JsonIgnoreProperties("notes")
    Document document;

    String author;
    String content;
}
