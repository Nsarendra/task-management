# Task Management REST API
A RESTful Task Management API built using **Java 17**, **Spring Boot 3**, **Spring Data JPA**, and an **H2 in-memory database**. This application allows users to create, retrieve, update, delete, and complete tasks through REST endpoints. Basic Authentication has been implemented using Spring Security.

## Features
- Create a new task
- Retrieve all tasks
- Retrieve a task by ID
- Update an existing task
- Delete a task
- Mark a task as completed
- Input validation using Jakarta Validation
- Global exception handling
- H2 in-memory database
- Basic Authentication using Spring Security
- Unit testing using JUnit 5 and Mockito

## Technology Stack

| Technology | Version |
|------------|---------|
| Java | 17 |
| Spring Boot | 3.x |
| Spring Data JPA | Latest |
| Spring Security | Latest |
| H2 Database | In-Memory |
| Maven | Build Tool |
| JUnit 5 | Testing |
| Mockito | Unit Testing |


## Project Structure

src
├── main
│   ├── java/com/example/taskmanager
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── exception
│   │   ├── mapper
│   │   ├── model
│   │   ├── repository
│   │   ├── service
│   │   └── TaskManagerApplication.java
│   └── resources
│       └── application.yml
│
└── test
    └── java/com/example/taskmanager/service
        └── TaskServiceImplTest.java


## How to Run the Application

### Prerequisites

- Java 17
- Maven 3.8+
- IntelliJ IDEA (Recommended)

### Steps

1. Clone the repository.

```bash
git clone <repository-url>
```

2. Navigate to the project.

```bash
cd task-manager
```

3. Build the project.

```bash
mvn clean install
```

4. Run the application.

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

## H2 Database Console

The application uses an **H2 in-memory database**.

Open:

```text
http://localhost:8080/h2-console
```

Use the following credentials:

| Property | Value |
|----------|-------|
| JDBC URL | jdbc:h2:mem:taskdb |
| Username | sa |
| Password | *(leave blank)* |

## Authentication

Basic Authentication is enabled for all REST endpoints.

| Username | Password |
|----------|----------|
| admin | password123 |

Use **Basic Auth** in Postman or any REST client.

---

## API Endpoints

### 1. Get All Tasks

**GET**

```http
GET /tasks
```

Response: **200 OK**


### 2. Get Task By ID

**GET**

```http
GET /tasks/{id}
```

Response: **200 OK**



### 3. Create Task

**POST**

```http
POST /tasks
```

Request Body

```json
{
  "title": "Task 1",
  "description": "Finish Task 1 implementation",
  "dueDate": "2026-09-20"
}
```

Response: **201 Created**

---

### 4. Update Task

**PUT**

```http
PUT /tasks/{id}
```

Request Body

```json
{
  "title": "Updated Task 1",
  "description": "Updated Task1 description",
  "dueDate": "2026-09-25",
  "status": "IN_PROGRESS"
}
```

Response: **200 OK**

---

### 5. Delete Task

**DELETE**

```http
DELETE /tasks/{id}
```

Response: **204 No Content**

---

### 6. Mark Task as Complete

**PATCH**

```http
PATCH /tasks/{id}/complete
```

Response: **200 OK**

---

## Task Model

```json
{
  "id": 1,
  "title": "Task 1",
  "description": "Task 1 implementation",
  "dueDate": "2026-09-20",
  "status": "PENDING",
  "createdAt": "2026-09-10T11:20:00",
  "updatedAt": "2026-09-10T11:20:00"
}
```

### Task Status

- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`

---

## Validation Rules

| Field | Validation |
|-------|------------|
| title | Required, 3–100 characters |
| description | Required, 5–500 characters |
| dueDate | Required, present or future date |
| status | Optional, valid enum value |

---

## Error Handling

The application uses a global exception handler for consistent error responses.

### Validation Error (400)

```json
{
  "timestamp": "2026-09-10T12:10:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Validation Failed",
  "path": "/tasks",
  "validationErrors": {
    "title": "Title is required."
  }
}
```

### Resource Not Found (404)

```json
{
  "timestamp": "2026-09-10T12:15:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Task not found with id: 10",
  "path": "/tasks/10"
}
```

---

## Unit Tests

The service layer is tested using **JUnit 5** and **Mockito**.

### Run Tests

```bash
mvn test
```

The tests cover:

- Task creation
- Retrieve all tasks
- Retrieve task by ID
- Update task
- Delete task
- Mark task as completed
- Success and failure scenarios

---

## Design Decisions

- Used a layered architecture (**Controller → Service → Repository**).
- Used **DTOs** to separate API contracts from entity models.
- Used **Spring Data JPA** with an **H2 in-memory database** to satisfy the assignment requirement.
- Used **Global Exception Handling** with `@RestControllerAdvice`.
- Implemented **Basic Authentication** using Spring Security.

---

## Assumptions

- The H2 database is recreated every time the application starts.
- Task IDs are generated automatically by H2.
- Authentication uses a single in-memory admin user for demonstration purposes.
- No user management or registration functionality is included.
