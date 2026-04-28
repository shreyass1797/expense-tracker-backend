Enterprise Expense Tracker API 💰

A production-grade RESTful API built with Java and Spring Boot, designed to securely manage, aggregate, and paginate user financial data. This project implements advanced SDE-1 backend patterns including stateless JWT authentication, custom JPQL aggregations, and global exception handling.
🚀 Key Features

    Stateless Security: Implements token-based authentication using JSON Web Tokens (JWT) and Spring Security, eliminating server-side session memory overhead.

    Smart Pagination: Utilizes Spring Data's Pageable interface to deliver transaction history in manageable, sorted chunks, preventing database memory exhaustion at scale.

    Advanced Aggregations: Leverages custom JPQL queries to offload heavy mathematical operations to the database, returning precisely aggregated financial summaries (e.g., total spend per category).

    Business Logic Enforcement: Actively intercepts transactions to enforce user-defined monthly budgets, preventing data commits that violate financial parameters.

    Global Safety Net: Features a @ControllerAdvice layer to catch runtime exceptions (like budget violations or duplicate usernames) and return standardized, frontend-friendly JSON error payloads instead of raw server stack traces.

🛠️ Tech Stack

    Language: Java 17

    Framework: Spring Boot 3.x

    Database: PostgreSQL

    ORM: Spring Data JPA / Hibernate

    Security: Spring Security & jjwt (HMAC SHA-256)

    Build Tool: Maven

🏗️ Architectural Highlights
1. Context-Aware Data Linking

By extracting the user's identity directly from the cryptographically verified JWT (SecurityContextHolder), the API securely links expenses to the correct user in the PostgreSQL database without ever exposing sensitive userId parameters in the HTTP endpoints.
2. Defensive Programming

Custom exception classes (e.g., BudgetExceededException) work in tandem with the Global Exception Handler to ensure that the API always responds with predictable, clean 400 Bad Request or 404 Not Found JSON structures, ensuring a seamless integration experience for frontend clients.
🛣️ Core Endpoints

Authentication (/api/auth)

    POST /register - Secure user registration with BCrypt password hashing.

    POST /login - Credential verification and JWT generation.

Expense Engine (/api/expenses)

    POST / - Add a new expense (Protected, Enforces Monthly Budget).

    GET / - Retrieve user expenses (Protected, Paginated, Sorted by Date).

    GET /summary - Retrieve aggregated total spend grouped by category (Protected, JPQL optimized).