package at.fhtw.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

}
