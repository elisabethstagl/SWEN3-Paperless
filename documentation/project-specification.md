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

The backend structure shown below represents the current state of the project. As development progresses, DTOs and mappers will be introduced to provide a cleaner separation between the REST API, business logic, and persistence layer.
```text
backend/src/main/java/at/fhtw/backend/
├── business/               # Business logic / services
├── controller/             # REST controllers
├── model/                  # JPA entities
├── persistence/            # Data Access Layer / repositories
└── BackendApplication.java
```
---

## Configuration

Sensitive configuration data, such as database credentials, is stored in a local `.env` file. This file is excluded from Git to prevent passwords and other sensitive values from being committed to the repository.

An `.env.example` file is provided as a template and contains all required environment variables without sensitive values. To configure the application locally, copy `.env.example` to `.env` and fill in the appropriate values.

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



---

# Testing

## Unit Tests

Unit tests are implemented using:

- JUnit
- Mockito

The production database is not accessed by the service unit tests. Instead, `DocumentRepository` is mocked:
This allows the business logic to be tested independently of PostgreSQL.

Current unit tests cover:

- retrieving all documents
- retrieving an existing document
- retrieving a non-existing document
- deleting an existing document
- deleting a non-existing document

---

# Additional Use Case

Users can add notes to their files.

## Description


The additional use case allows users to create and manage notes associated with their documents. A document can have multiple notes, which can be used to store comments, reminders, or other additional information related to the document.

Each note is stored as a separate entity and is associated with a specific document. Users can create, view, update, and delete notes.

# Tracked Time

## Elisabeth Stagl

### Sprint 1

| Task / Feature                                   | Time (h) |
|--------------------------------------------------|:--------:|
| Git Repository Setup and Spring Boot Integration |   0.5    |
| Docker Setup                                     |   2.0    |
| Small Changes                                    |   0.5    |
| Additional Use Case                              |   1.0    |
| Protocol / Project Specification                 |   1.0    |
| **Subtotal**                                     | **0.0**  |

## Maximilian Jandl

### Sprint 1

| Task / Feature                                   | Time (h) |
|--------------------------------------------------|:--------:|
| Git Repository Setup and Spring Boot Integration |   0.5    |
| Docker Setup                                     |   2.0    |
| Small Changes                                    |   0.5    |
| Additional Use Case                              |   1.0    |
| Protocol / Project Specification                 |   1.0    |
| **Subtotal**                                     | **0.0**  |