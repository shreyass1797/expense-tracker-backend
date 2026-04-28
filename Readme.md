# Enterprise Expense Tracker API 💰

A production-grade RESTful API built with **Java** and **Spring Boot**, designed to securely manage, aggregate, and paginate user financial data. This project implements advanced SDE-1 backend patterns including stateless JWT authentication, custom JPQL aggregations, and global exception handling.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [API Reference](#api-reference)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Security Model](#security-model)
- [Error Handling](#error-handling)

---

## Features

| Feature | Description |
|---|---|
| 🔐 **Stateless Security** | Token-based auth via JWT + Spring Security — no server-side session overhead |
| 📄 **Smart Pagination** | Spring Data `Pageable` delivers sorted transaction history in manageable chunks |
| 📊 **Advanced Aggregations** | Custom JPQL queries offload heavy math to the database, returning aggregated category summaries |
| 🚧 **Budget Enforcement** | Intercepts transactions in real-time to block commits that violate user-defined monthly budgets |
| 🛡️ **Global Exception Handling** | `@ControllerAdvice` catches runtime exceptions and returns clean, standardized JSON error payloads |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + `jjwt` (HMAC SHA-256) |
| Build Tool | Maven |

---

## Architecture

### Context-Aware Data Linking

User identity is extracted directly from the cryptographically verified JWT via `SecurityContextHolder`. This means expenses are securely linked to the correct user in PostgreSQL **without ever exposing sensitive `userId` parameters** in HTTP endpoints — eliminating a whole class of IDOR vulnerabilities by design.

```
Request → JWT Filter → SecurityContextHolder → Service Layer → DB
                            ↑
                   userId extracted here,
                   never from request body
```

### Defensive Programming

Custom exception classes work in tandem with the Global Exception Handler to guarantee that the API **always** responds with predictable, structured JSON — regardless of what goes wrong internally.

```
BudgetExceededException  ──┐
ResourceNotFoundException  ──┤──▶  @ControllerAdvice  ──▶  { "error": "...", "status": 400 }
DuplicateUsernameException ──┘
```

---

## API Reference

### Authentication — `/api/auth`

#### `POST /api/auth/register`
Registers a new user. Passwords are hashed with **BCrypt** before persistence.

**Request Body**
```json
{
  "username": "shreyass",
  "password": "securepassword123"
}
```

**Response — `201 Created`**
```json
{
  "message": "User registered successfully."
}
```

---

#### `POST /api/auth/login`
Verifies credentials and returns a signed JWT.

**Request Body**
```json
{
  "username": "shreyass",
  "password": "securepassword123"
}
```

**Response — `200 OK`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### Expense Engine — `/api/expenses`

> All expense endpoints are **protected**. Include the JWT in the `Authorization` header:
> ```
> Authorization: Bearer <your_token>
> ```

#### `POST /api/expenses/`
Adds a new expense for the authenticated user. Enforces the user's monthly budget before committing.

**Request Body**
```json
{
  "amount": 1500.00,
  "category": "Food",
  "description": "Team lunch",
  "date": "2025-06-15"
}
```

**Response — `201 Created`**
```json
{
  "id": 42,
  "amount": 1500.00,
  "category": "Food",
  "description": "Team lunch",
  "date": "2025-06-15"
}
```

**Response — `400 Bad Request` (Budget Exceeded)**
```json
{
  "error": "BudgetExceededException",
  "message": "This expense exceeds your monthly budget of ₹10,000.",
  "status": 400
}
```

---

#### `GET /api/expenses/`
Retrieves the authenticated user's expenses, paginated and sorted by date (descending).

**Query Parameters**

| Param | Type | Default | Description |
|---|---|---|---|
| `page` | `int` | `0` | Page number (zero-indexed) |
| `size` | `int` | `10` | Records per page |

**Example Request**
```
GET /api/expenses/?page=0&size=10
```

**Response — `200 OK`**
```json
{
  "content": [
    {
      "id": 42,
      "amount": 1500.00,
      "category": "Food",
      "description": "Team lunch",
      "date": "2025-06-15"
    }
  ],
  "totalPages": 5,
  "totalElements": 47,
  "number": 0,
  "size": 10
}
```

---

#### `GET /api/expenses/summary`
Returns aggregated total spend grouped by category for the authenticated user. Powered by a custom JPQL query — computation happens at the database layer for optimal performance.

**Response — `200 OK`**
```json
[
  { "category": "Food",      "totalSpend": 8200.00 },
  { "category": "Transport", "totalSpend": 3450.50 },
  { "category": "Utilities", "totalSpend": 2100.00 }
]
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/enterprise-expense-tracker.git
cd enterprise-expense-tracker
```

### 2. Configure the Database

Create a PostgreSQL database:
```sql
CREATE DATABASE expense_tracker;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_tracker
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update

app.jwt.secret=your_256_bit_secret_key_here
app.jwt.expiration-ms=86400000
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## Project Structure

```
src/main/java/com/example/expensetracker/
├── controller/
│   ├── AuthController.java
│   └── ExpenseController.java
├── service/
│   ├── AuthService.java
│   └── ExpenseService.java
├── repository/
│   └── ExpenseRepository.java        # Custom JPQL aggregation queries
├── model/
│   ├── User.java
│   └── Expense.java
├── security/
│   ├── JwtUtil.java                  # Token generation & validation
│   ├── JwtAuthFilter.java            # Request filter chain
│   └── SecurityConfig.java
├── exception/
│   ├── BudgetExceededException.java
│   ├── DuplicateUsernameException.java
│   └── GlobalExceptionHandler.java   # @ControllerAdvice
└── dto/
    ├── RegisterRequest.java
    ├── LoginRequest.java
    ├── ExpenseRequest.java
    └── CategorySummaryDto.java
```

---

## Security Model

Authentication follows a stateless JWT flow:

```
1. Client sends credentials  →  POST /api/auth/login
2. Server validates, signs JWT with HMAC-SHA256
3. Client stores token, attaches to every subsequent request
   Authorization: Bearer <token>
4. JwtAuthFilter intercepts request, validates signature
5. User identity loaded into SecurityContextHolder
6. Controller/Service reads identity — no DB lookup needed
```

Token signing uses **HMAC SHA-256**, ensuring tokens cannot be forged without the server secret. All passwords are stored as **BCrypt hashes** — plaintext is never persisted.

---

## Error Handling

All errors return a consistent JSON structure, making frontend integration predictable:

```json
{
  "error": "ExceptionClassName",
  "message": "Human-readable description of what went wrong.",
  "status": 400
}
```

| Scenario | HTTP Status |
|---|---|
| Budget limit exceeded | `400 Bad Request` |
| Username already exists | `400 Bad Request` |
| Expense / resource not found | `404 Not Found` |
| Invalid or expired JWT | `401 Unauthorized` |
| Accessing another user's data | `403 Forbidden` |

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.