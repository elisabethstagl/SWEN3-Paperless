# Paperless – Document Management System

Semester project for **Software Engineering 3 (SWEN3)** at FH Technikum Wien.

Paperless is a document management system that allows users to upload, manage, search, and process PDF documents. Documents are persisted in PostgreSQL and will later be processed using OCR, full-text search, and AI-generated summaries.

### Team members:
* Jandl Maximilian
* Stagl Elisabeth

---

# Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 25 | Programming language |
| Spring Boot 4 | Backend framework |
| Spring Web MVC | REST API |
| Spring Data JPA | Persistence / repository abstraction |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Maven | Build and dependency management |
| Docker | Containerization |
| Docker Compose | Local infrastructure |
| JUnit | Unit testing |
| Mockito | Mocking dependencies in unit tests |
| iText | PDF metadata extraction |
| Lombok | Reduction of Java boilerplate |
| Bruno / Postman | Manual REST API testing |

Additional technologies such as RabbitMQ, MinIO, Elasticsearch, OCR, and GenAI will be integrated in later sprints.

---

## Project Structure

### Backend Structure

The backend structure shown below represents the current state of the project.
```text
backend/src/main/java/at/fhtw/backend/
├── business/               # Business logic / services
├── dto/                    # DTOs
├── mapper/                 # Mappers (using MapStruct)
├── presentation/           # REST controllers
├── model/                  # JPA entities
├── persistence/            # Data Access Layer / repositories
└── BackendApplication.java
```
---

## Configuration

Sensitive configuration data, such as database credentials, is stored in a local `.env` file. This file is excluded from Git to prevent passwords and other sensitive values from being committed to the repository.

An `.env.example` file is provided as a template and contains all required environment variables without sensitive values. To configure the application locally, copy `.env.example` to `.env` and fill in the appropriate values.

---
## Running the Application

### 1. Configure Environment Variables

The project contains an `.env.example` file with the required environment variables.

Create a copy of `.env.example` and rename it to:

```text
.env
```

Fill in the required values in the `.env` file. The `.env` file contains local configuration such as database credentials and is excluded from Git.

### 2. Start the Application

From the project root directory, start the complete application and its required infrastructure using Docker Compose:

```bash
docker compose --profile full up --build -d
```

This builds the required Docker images and starts all services of the `full` profile in detached mode.

### 3. Test the REST API

A Postman collection is included in the project for testing the available REST endpoints.

Import the provided Postman collection into Postman. The collection contains requests for the document and note endpoints and can be used to manually test the REST API after the application has started.

### 4. Stop the Application

To stop and remove the containers started by Docker Compose, run:

```bash
docker compose --profile full down
```

To start the application again, run:

```bash
docker compose --profile full up --build -d
```

---

# REST API

Base path:

```text
/api/documents
```

## Document Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/documents` | Get all documents |
| `GET` | `/api/documents/{id}` | Get document by ID |
| `POST` | `/api/documents` | Upload a PDF document |
| `PUT` | `/api/documents/{id}` | Replace/update a document |
| `DELETE` | `/api/documents/{id}` | Delete a document |


## Note Endpoints

| Method | Endpoint                                      | Description |
|--------|-----------------------------------------------|-------------|
| `GET` | `/api/documents/{documentId}/notes`           | Get all notes for a document |
| `GET` | `/api/documents/{documentId}/notes/{noteId}`  | Get a note by ID |
| `POST` | `/api/documents/{documentId}/notes`          | Add a note to a document |
| `PUT` | `/api/documents/{documentId}/notes/{noteId}`  | Update a note |
| `DELETE` | `/api/documents/{documentId}/notes/{noteId}` | Delete a note |


---

# Testing
## Unit Tests

Unit tests are implemented using:

- JUnit
- Mockito

The production database is not accessed by the service unit tests. Instead, DocumentRepository, NoteRepository, and the corresponding mappers are mocked.

This allows the business logic to be tested independently of PostgreSQL.

### DocumentService Tests

Current unit tests cover:

- retrieving all documents
- retrieving an existing document
- retrieving a non-existing document
- deleting an existing document
- deleting a non-existing document

### NoteService Tests

Current unit tests cover:

- retrieving all notes for a document
- retrieving an existing note
- retrieving a non-existing note
- handling a note that belongs to a different document
- adding a note to an existing document
- handling note creation for a non-existing document
- updating an existing note
- deleting an existing note
- deleting a non-existing note

### Mapper Tests

Mapper functionality is tested separately using the actual MapStruct mapper implementations.

These tests verify:

- conversion from entities to DTOs
- conversion from DTOs to entities
- update mappings
- fields that are intentionally ignored during mapping
---

# Additional Use Case

Users can add notes to their files.

## Description


The additional use case allows users to create and manage notes associated with their documents. A document can have multiple notes, which can be used to store comments, reminders, or other additional information related to the document.

Each note is stored as a separate entity and is associated with a specific document. Users can create, view, update, and delete notes.

# Tracked Time

## Elisabeth Stagl

### Sprint 1

| Task / Feature                                    | Time (h) |
|---------------------------------------------------|:--------:|
| Git Repository Setup and Spring Boot Integration  |   0.5    |
| Docker Setup                                      |   2.0    |
| Small Changes                                     |   0.5    |
| Additional Use Case                               |   1.0    |
| DTOs and Mappers, changes in Service + Controller |   3.0    |
| Added/changed tests + Test for Mappers            |   2.5    |
| Protocol / Project Specification                  |   2.0    |
| **Subtotal**                                      | **11.5** |

### Sprint 2

| Task / Feature                   | Time (h) |
|----------------------------------|:--------:|
| Frontend Setup + Styling         |   3.0    |
| Protocol / Project Specification |   0.0    |
| **Subtotal**                     | **0.0**  |

## Maximilian Jandl

### Sprint 1 (not up-to-date!)

| Task / Feature                                   | Time (h) |
|--------------------------------------------------|:--------:|
| Git Repository Setup and Spring Boot Integration |   0.5    |
| Docker Setup                                     |   2.0    |
| Small Changes                                    |   0.5    |
| Additional Use Case                              |   1.0    |
| Protocol / Project Specification                 |   1.0    |
| **Subtotal**                                     | **0.0**  |