package at.fhtw.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document")
public class Document {
    @Id
    UUID id;

    @Column
    String filename;

    @Column
    String author;

    @Column
    String creator;

    @Column
    Integer pages;

    @Column
    Date createdOn;

    @Column
    String summary;

    @OneToMany(mappedBy = "document", orphanRemoval=true)
    @JsonIgnoreProperties("document")
    List<Note> notes;

}
