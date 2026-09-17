package at.fhtw.backend.persistence;

import at.fhtw.backend.model.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends CrudRepository<Document, UUID> {

}
