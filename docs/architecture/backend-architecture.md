# Backend architecture

This diagram expands the Spring Boot portion of [`salary-management.png`](../../salary-management.png) into its API, security, application, persistence, and operational responsibilities.

```mermaid
flowchart LR
  WEB[Angular web app]

  subgraph Security[Authentication and request security]
    LOGIN[AuthController]
    AUTH[AuthService]
    CONFIG[SecurityConfiguration\nHR account · BCrypt · AuthenticationManager · JWT encoder/decoder]
    FILTER[Spring Security JWT resource server\nValidate bearer token and role]
    LOGIN --> AUTH
    CONFIG -->|AuthenticationManager and JwtEncoder| AUTH
  end

  subgraph API[REST API]
    EMPAPI[EmployeeDirectoryController]
    SALAPI[SalaryHistoryController]
    ANALYTICSAPI[SalaryAnalyticsController]
    ACTUATOR[Actuator health and loggers]
    DOCS[OpenAPI and Swagger UI]
  end

  subgraph Application[Application services]
    EMPAPP[Employee directory service]
    SALAPP[Salary history service]
    ANALYTICSAPP[Salary analytics service]
  end

  subgraph Persistence[Repositories and domain]
    EMPREPO[EmployeeRepository]
    SALREPO[SalaryHistoryRepository]
    ENTITIES[Employee and SalaryHistory entities]
  end

  DB[(PostgreSQL)]
  MIGRATIONS[Flyway migrations]
  CROSS[GlobalExceptionHandler · validation · structured logs]
  OPERATOR[Developer or API consumer]

  WEB -->|POST /api/auth/login| LOGIN
  AUTH -->|JWT response| WEB
  WEB -->|Bearer JWT| FILTER
  FILTER --> EMPAPI
  FILTER --> SALAPI
  FILTER --> ANALYTICSAPI
  EMPAPI --> EMPAPP --> EMPREPO
  SALAPI --> SALAPP --> SALREPO
  ANALYTICSAPI --> ANALYTICSAPP --> SALREPO
  EMPREPO --> ENTITIES
  SALREPO --> ENTITIES
  ENTITIES --> DB
  MIGRATIONS --> DB
  CROSS -. handles API errors .-> EMPAPI
  CROSS -. handles API errors .-> SALAPI
  CROSS -. handles API errors .-> ANALYTICSAPI
  OPERATOR --> ACTUATOR
  OPERATOR --> DOCS
```

## Responsibilities

- **API controllers** handle HTTP contracts and map requests and responses.
- **Application services** enforce use cases such as validating salary changes and generating as-of-date analytics.
- **Repositories and entities** persist employee and effective-dated salary history data. The analytics repository performs database-side aggregation.
- **Security configuration** authenticates the configured HR manager and validates JWT bearer tokens for protected routes.
- **Flyway** creates and evolves the relational schema. The demo seed script is separate from production migrations.
- **Cross-cutting facilities** provide consistent API errors, validation, logs, health/diagnostic endpoints, and generated API documentation.
