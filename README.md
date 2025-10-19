[![codecov](https://codecov.io/gh/your-username/your-repo/branch/main/graph/badge.svg)](https://codecov.io/gh/your-username/your-repo)

# 🐯 Maybank Batch Processing

A simple **Spring Boot** project simulating batch transaction processing for demonstration purposes.

This application showcases:
- Batch file import using **Spring Batch**
- RESTful API design for viewing and updating transactions
- Data transfer layer via **DTOs**
- Filtering and pagination with **Spring Data JPA Specifications**
- Integration with **H2 Database (file-based)**
- **Optimistic locking** for concurrent updates on the Update API
- **Swagger UI** for API documentation and testing

---

## ⚙️ Features

- 🔁 **Batch transaction processing**
  - Reads `|`-delimited transaction data file
  - Parses and writes transactions in chunks of 10
  - Automatically runs once at startup (skipped if data already imported)

- 🔍 **Filtering & Pagination**
  - Search and filter transactions by fields like `accountNumber`, `customerId`, etc.
  - Pagination and sorting supported

- 🧩 **DTO & Mapper Layer**
  - Separation between entity and response/request models for cleaner architecture
 
- 🔐 **Concurrent Update Handling**
  - Uses Optimistic Locking via JPA @Version field
  - Prevents accidental overwriting of records when multiple users attempt to update the same transaction concurrently
  - Returns HTTP 409 Conflict on version mismatch

- 🧠 **Error Handling**
  - Centralized via `GlobalExceptionHandler`
  - Custom exceptions for 404 and 409 scenarios

- 🧾 **Swagger UI Integration**
  - Interactive API testing via browser

---

## 🧰 Tech Stack

| Layer | Technology |
|-------|-------------|
| Backend | Java 17, Spring Boot 3 |
| Batch Processing | Spring Batch |
| Database | H2 (file-based, embedded) |
| Security | Spring Security (Basic Auth) |
| Testing | JUnit 5 |
| Documentation | Swagger UI |
| Build Tool | Maven |

---

## Design Patterns Used

| Pattern | Description | Reason for Use |
|----------|--------------|----------------|
| **Layered Architecture** | Application is structured into Controller → Service → Repository layers. | Ensures clean separation of concerns and improves maintainability. |
| **Repository Pattern** | `TransactionRecordRepository` handles data access via Spring Data JPA. | Abstracts database logic from business logic for cleaner code. |
| **Service Pattern** | `TransactionRecordService` and `TransactionRecordServiceImpl` encapsulate business logic. | Keeps controllers lightweight and promotes reusability. |
| **DTO (Data Transfer Object)** | Request and Response DTOs (`TransactionRecordRequest`, `TransactionRecordResponse`) separate entity and API representations. | Improves security and flexibility for API data exposure. |
| **Mapper Pattern** | `TransactionRecordMapper` converts between entities and DTOs. | Centralizes mapping logic, ensuring consistent transformation across layers. |
| **Singleton Configuration** | `BatchConfig` and `SecurityConfig` are singletons managed by Spring. | Ensures consistent configuration across the application. |
| **Strategy Pattern (Spring Batch)** | Reader and Writer components act as interchangeable strategies within the batch job. | Promotes modularity — allows different data sources or destinations without changing job logic. |
| **Optimistic Locking (Concurrency Control)** | `TransactionRecord` entity includes a `@Version` field for version tracking. | Safely handles concurrent updates, ensuring data consistency and preventing lost updates. |

---

## Design Rationale

This project follows a **modular, layered design** to ensure testability and scalability.  
Each major concern (API, service, persistence, and batch processing) is isolated, allowing easy maintenance and future extension.  
Spring Batch provides a structured **chunk-oriented processing model**, while DTO and Mapper layers prevent entity leakage across boundaries.  
This combination promotes clean architecture and reduces coupling between components.

---

## 🗄️ Access H2 Database

| Setting | Value |
|----------|--------|
| URL | [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/) |
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:file:./data/maybankdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE` |
| Username | `sa` |
| Password | *(leave blank)* |

📦 The database file (`maybankdb.mv.db`) is stored in the `/data` folder and persists between runs.  
Delete the file to reset all data (IDs will restart from 1).

---

## 🔐 Access Swagger UI

URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
**Basic Authentication:**

| Setting | Value |
|----------|--------|
| Username | `admin` |
| Password | `password` |

---

## 🧩 Diagrams

Class, Activity, and Sequence diagrams are located in the [`/diagram`](./diagram) folder.

---

## 📝 Notes

- H2 database is persistent locally under `/data`.
- Credentials, file paths, and ports can be modified in `application.yml`.
- The batch job runs automatically on startup unless data already exists.
- To re-import data, delete the `transaction_record` table or the `.mv.db` file.

---

## 🚀 How to Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

Access the APIs via Swagger or any REST client.
```

## 🧪 Running Tests

```bash
mvn test
```
