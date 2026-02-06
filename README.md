# Task Manager API

Backend REST API for a task management application designed to work with multiple workspaces, users and roles.  
The project is built following a layered architecture with clean code principles,
designed to evolve towards a hexagonal or microservices architecture

---

## Why this project?

This project was designed to demonstrate:
- Domain-driven thinking and relational data modeling
- Real-world authorization scenarios (users, roles, workspaces)
- Clean separation of concerns in a Spring Boot application
- Scalability considerations from early stages

---

## Features

- User management with global roles
- Workspace-based task organization
- Role-based access inside each workspace
- Task assignment (main user + secondary users)
- Task states and tags
- Audit fields (creation & update timestamps)
- Prepared for Spring Security and JWT authentication

---

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Security
- MySQL
- Lombok
- Bean Validation (Jakarta Validation)
- Maven

---

## Architecture

The project follows a layered architecture:
- controller → REST controllers
- service → Business logic
- dto → Data Transfer Objects
- persistence
  - model → JPA entities
  - repository → Spring Data repositories
- security → Security configuration
- exception → Global and custom exceptions
- enums → Application enums shared across layers

---

## Domain Model

- **User**
  - Global application user
  - Authentication and authorization responsibilities

- **Workspace**
  - Logical space that groups users and tasks

- **WorkspaceUser**
  - Association entity between User and Workspace
  - Defines user role and status inside a workspace

- **Task**
  - Belongs to a workspace
  - Assigned to one main user and multiple secondary users
  - Supports states, tags, and scheduling

- **Tag**
  - Used to categorize tasks
  - Includes color metadata

---

## Database

- MySQL
- JPA/Hibernate auto schema generation
- Join tables for many-to-many relationships

---

## How to Run the Project

### Prerequisites
- Java 17
- MySQL running locally
- Maven

### Steps

1. Create database:
```sql
CREATE DATABASE task_manager;
```
2. Configure application.yml with your database credentials.

3. Run the application:

```bash
mvn spring-boot:run
```
The API will be available at:

```arduino
http://localhost:8080
```
## Security (Work in Progress)

- Spring Security configured
- JWT-based authentication (access & refresh tokens)
- Password encryption using BCrypt
- Role-based authorization per workspace
- Workspace-level authorization checks


## Roadmap

Phase 1 – Domain & Persistence
- 👍 Domain model design
- 👍 JPA entities and relationships
- Repository layer

Phase 2 – API Layer
- DTO layer
- REST controllers
- Global exception handling

Phase 3 – Security
- JWT authentication (access & refresh tokens)
- Password encryption (BCrypt)
- Role-based authorization per workspace

Phase 4 – Core Features
- Task scheduling (date & time slots)
- Task state transitions
- Advanced task filtering

Phase 5 – Documentation & Frontend
- OpenAPI / Swagger
- Angular frontend


## Notes
This project is part of a personal portfolio and is continuously evolving to reflect best practices in backend development.
