# JWT Authentication System with Spring Boot & Redis

In standard stateless JWT applications, issued tokens cannot be invalidated until they naturally expire, creating a critical security loophole if an access token is intercepted or a user logs out.

This project solves that flaw by implementing a Hybrid Validation Lifecycle:

1. Dual Token Lifecycles - Access Token (Stateless): Short-lived token (15 minutes) passed via HTTP Authorization: Bearer headers to authenticate protected endpoints with zero relational database latency.
Refresh Token (Stateful Whitelist): Long-lived token (7 days) saved securely inside Redis. On token refresh requests, the system validates the incoming token string against the live memory whitelist before authorizing new access keys.

2. Instant Token Revocation (Blacklisting) - When an explicit /logout event is triggered, the short-lived access token is captured and committed directly to Redis as a temporary Blacklisted key. The key's Time-to-Live (TTL) is dynamically calculated to match the exact remaining lifespan of the token, ensuring automatic clean-up while blocking unauthorized usage immediately.

![System Architecture](system-architecture/hld%20diagram.png)

## Ecosystem
* Backend Framework: Spring Boot v4.0.6 / Java 24
* Security Engine: Spring Security (Stateless filter chain assembly)
* Database Persistent Layer: PostgreSQL (User records, structural indexing)
* Caching & Session Database: Redis Server (High-speed TTL key-value caching)
* Cryptographic Layer: io.jsonwebtoken (JJWT Primitives)
* Object-Relational Mapping: Spring Data JPA / Hibernate
* Database Connection Pool: HikariCP

## 🌟 Key Features

* **Dual-Token Hybrid Lifecycle:** Implements a cutting-edge hybrid security framework balancing stateless performance with stateful session control. Uses short-lived (15-minute) **JWT Access Tokens** for instantaneous, zero-latency microservice resource guard matching, paired with long-lived (7-day) **Refresh Tokens** to sustain long-term sessions securely.
* **Active Redis Whitelisting:** Eliminates standard JWT vulnerabilities by maintaining an immutable user refresh token session map directly inside high-speed **Redis RAM storage**. Token validation chains inspect the active Redis whitelist dynamically on every rotation call, locking out hijacked or compromised long-lived signatures instantly.
* **Dynamic Request Interception & Blacklisting:** Features instant user session revocation on explicit account logouts. The system captures the user's current short-lived access token and commits it to a **Redis Blacklist** with a precise, dynamic Time-To-Live (TTL) countdown matching the token’s exact remaining lifecycle, closing the gate against reuse immediately.
* **Zero-Trust Security Context Integration:** Integrates fully custom `JwtFilter` request processing layers directly into Spring Security's native execution pipeline. Successfully validated incoming token claims seamlessly inject type-safe domain identities into the system context via `@AuthenticationPrincipal`, eliminating manual data extraction or redundant database lookups across protected controller endpoints.
* **Non-Blocking Text-Serialized RAM Access:** Utilizes plain-text key-value space mapping and standard string serialization inside the memory database engine. This ensures all background caching, session checks, and blacklisting operations execute at maximum sub-millisecond network speeds without data structure parsing overhead.

![Class Diagram](system-architecture/lld%20diagram.png)

## Tech Stack & Tools

* **Backend Framework:** Java 24 / Spring Boot 4.x (Spring Data JPA, Spring Security)
* **Caching & In-Memory Session Storage:** Redis Open-Source Cache (WSL-Ubuntu Native Native Subsystem / Docker)
* **Database Persistent Layer:** PostgreSQL Engine (Relational identity models, indexing)
* **API Verification & Contracts:** Postman Client v10 Engine

![database Schema](system-architecture/db%20diagram.png)

## Core Dependencies Used
* spring-boot-starter-web
* spring-boot-starter-data-jpa
* spring-boot-starter-data-redis
* spring-boot-starter-security
* postgresql (JDBC Driver)
* lombok
* jjwt-api
* jjwt-impl
* jjwt-jackson

### API Specifications & Contract

The backend exposes a structured, RESTful API contract. All protected endpoints expect a valid JWT token passed via the `Authorization: Bearer <token>` header.

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/auth/register` | Public | Registers a new user identity inside the PostgreSQL database. |
| **POST** | `/api/v1/auth/login` | Public | Authenticates user credentials, generates the stateless token pair, and whitelists the refresh session in Redis. |
| **GET** | `/api/v1/dashboard/info` | Protected | Extracts identity context securely via `@AuthenticationPrincipal` from the validated token and displays session-specific user data. |
| **GET** | `/api/v1/dashboard/admin-stats` | Protected | Enterprise verification route used to test custom authority validation and intercept non-authorized requests. |

