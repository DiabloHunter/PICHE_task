# Technology Choices for Banking API Project

## Introduction
This document explains the reasoning behind the selection of technologies and approaches used in the Banking API project. Each choice has been made to optimize for scalability, maintainability, and ease of development while addressing the requirements for transactional integrity and concurrency.

## Selected Technologies and Their Benefits

### **Java 21**
- **Reason**: Java 21 is the latest long-term support (LTS) version, offering enhanced performance, modern language features, and long-term stability.
- **Key Features**:
    - Virtual threads for concurrency support.
    - Improved garbage collection and memory management.
    - Support for structured concurrency.

### **Spring Boot**
- **Reason**: Spring Boot simplifies the development of RESTful APIs by providing out-of-the-box configurations and integrations.
- **Key Features**:
    - Embedded server (Tomcat) for running applications without additional setup.
    - Built-in dependency injection for clean and testable code.
    - Starter dependencies for rapid development (e.g., `spring-boot-starter-data-jpa`, `spring-boot-starter-web`).

### **H2 Database**
- **Reason**: An in-memory database is used for its simplicity and speed, making it ideal for prototyping and testing.
- **Key Features**:
    - Zero-configuration setup.
    - Fast read/write operations.
    - Support for SQL standards.
    - Enables unit and integration testing without external dependencies.

### **Maven**
- **Reason**: Maven is a widely used build tool that automates dependency management and project builds.
- **Key Features**:
    - Dependency management via Maven Central Repository.
    - Plugin support (e.g., JaCoCo for code coverage).
    - Simplified multi-environment builds.

### **Spring Data JPA**
- **Reason**: Spring Data JPA abstracts database access, reducing boilerplate code and enabling efficient data handling.
- **Key Features**:
    - Simplified CRUD operations.
    - Support for custom queries and dynamic query generation.
    - Transaction management integration with Spring.

### **ReentrantLock and Synchronization**
- **Reason**: Concurrency handling is crucial for a banking API to prevent race conditions and ensure consistency.
- **Key Features**:
    - `ReentrantLock` ensures thread-safe operations with fine-grained control over locks.
    - Synchronized blocks ensure sequential execution for critical sections.

### **JUnit 5 and AssertJ**
- **Reason**: Robust testing frameworks ensure code reliability and maintainability.
- **Key Features**:
    - JUnit 5 provides extensive testing annotations and support for parameterized tests.
    - AssertJ offers fluent and expressive assertions for better test readability.

### **JaCoCo**
- **Reason**: Code coverage analysis ensures that all critical paths in the code are tested.
- **Key Features**:
    - Integrated with Maven for seamless reporting.
    - Generates detailed HTML reports for visualizing code coverage.

## Design Decisions

### **Controller -> Service -> Repository Architecture**
- Ensures separation of concerns:
    - **Controller**: Handles API requests and responses.
    - **Service**: Implements business logic.
    - **Repository**: Interacts with the database.
- Facilitates testability and maintainability by isolating each layer.

### **Transactional Annotations**
- Ensures atomicity for operations, making them either fully complete or fully rollback on failure.
- Simplifies transaction management using Spring’s declarative approach.

### **Concurrency Management**
- **ReentrantLock** and synchronized blocks prevent race conditions in multi-threaded environments.
- Ensures thread-safe deposits, withdrawals, and transfers.

## Conclusion
The selected technologies and design choices are tailored to create a reliable, efficient, and maintainable banking API. This stack ensures the application meets the requirements for transactional integrity, concurrency handling, and ease of development. These technologies also allow for future scalability and adaptability as the project evolves.

