# Banking Solution API

## Overview
The Banking Solution API is a simple RESTful application for basic banking operations, such as creating accounts, making deposits, withdrawing funds, and transferring funds. It ensures transactional integrity, supports concurrent operations, and provides a robust testing suite.

## Features
- **Account Management**:
    - Create new accounts with an initial balance.
    - Retrieve account details by account number.
    - List all accounts.

- **Account Transactions**:
    - Deposit funds into accounts.
    - Withdraw funds from accounts.
    - Transfer funds between accounts.

- **Concurrency Management**:
    - Handles concurrent requests safely using synchronized locks and proper transaction management.

## Tech Stack
- **Java**: Version 21
- **Spring Boot**: For REST API development and dependency management.
- **H2 Database**: In-memory database for data persistence during runtime.
- **Maven**: For build automation and dependency management.
- **JUnit 5**: For unit and integration testing.
- **AssertJ**: For fluent assertions in tests.

## Design Choices
- **Controller -> Service -> Repository Architecture**: Ensures separation of concerns for better maintainability and scalability.
- **Transactional Integrity**: All operations are wrapped in transactions to ensure atomicity.
- **Concurrency Handling**: Reentrant locks and synchronized blocks ensure thread-safe operations for concurrent requests.
- **Unit and Integration Tests**: Cover various scenarios, including edge cases and concurrent transactions.

## Setup Instructions
### Prerequisites
1. **Java 21 JDK** installed.
2. **Maven** (3.8.8 or higher).

### Clone the Repository
```bash
git clone <repository-url>
cd <repository-folder>
```

### Build and Run the Application
1. Compile and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
2. Access the application at `http://localhost:8080`.

### Running Tests
To execute the test suite and view the results:
```bash
mvn test
```

### Generate Code Coverage Report
To generate and view the JaCoCo code coverage report:
```bash
mvn clean verify
```
The report will be available under `target/site/jacoco/index.html`.

## API Endpoints
### Account Management
- **Create Account**:
    - `POST /api/accounts`
    - Request Body:
      ```json
      {
        "accountNumber": "12345",
        "initialBalance": 1000.00
      }
      ```

- **Get Account Details**:
    - `GET /api/accounts/{accountNumber}`

- **List All Accounts**:
    - `GET /api/accounts`

### Account Transactions
- **Deposit Funds**:
    - `POST /api/accounts/{accountNumber}/deposit`
    - Request Body:
      ```json
      {
        "amount": 500.00
      }
      ```

- **Withdraw Funds**:
    - `POST /api/accounts/{accountNumber}/withdraw`
    - Request Body:
      ```json
      {
        "amount": 300.00
      }
      ```

- **Transfer Funds**:
    - `POST /api/accounts/transfer`
    - Request Body:
      ```json
      {
        "sourceAccountNumber": "12345",
        "targetAccountNumber": "67890",
        "amount": 200.00
      }
      ```