# Frontend architecture

This diagram expands the Angular portion of [`salary-management.png`](../../salary-management.png), showing routing, authentication state, feature pages, and the API boundary.

```mermaid
flowchart TB
  HR[HR manager in browser]

  subgraph Angular[Angular standalone application]
    ROUTER[Angular Router]
    LOGIN[LoginPage]
    GUARD[authGuard]
    SHELL[AppShell]
    NAV[WorkspaceSidebar\nDesktop collapse · mobile menu]
    DIRECTORY[EmployeeDirectoryPage]
    HISTORY[EmployeeSalaryHistoryPage]
    INSIGHTS[SalaryInsightsPage]

    AUTH[AuthService\nLogin · token · expiry]
    STORAGE[(sessionStorage)]
    INTERCEPTOR[authInterceptor\nAttach bearer token to API requests]
    HTTP[Angular HttpClient]

    EMPLOYEE_SERVICE[EmployeeDirectoryService]
    SALARY_SERVICE[SalaryHistoryService]
    INSIGHT_SERVICE[SalaryInsightsService]

    ROUTER --> LOGIN
    ROUTER --> GUARD --> SHELL
    GUARD --> AUTH
    SHELL --> NAV
    SHELL --> DIRECTORY
    SHELL --> HISTORY
    SHELL --> INSIGHTS

    LOGIN --> AUTH
    AUTH --> STORAGE
    AUTH --> HTTP
    DIRECTORY --> EMPLOYEE_SERVICE --> HTTP
    HISTORY --> SALARY_SERVICE --> HTTP
    INSIGHTS --> INSIGHT_SERVICE --> HTTP
    HTTP --> INTERCEPTOR
  end

  API[Spring Boot REST API]
  HR --> ROUTER
  INTERCEPTOR -->|POST login excludes bearer token| API
  INTERCEPTOR -->|Protected requests include JWT| API
  API -->|JSON responses| HTTP
```

## Responsibilities

- **Router and guard** load feature pages lazily and protect the `/app` workspace using the current session.
- **AppShell and WorkspaceSidebar** provide shared navigation, including a collapsible desktop sidebar and a mobile navigation menu.
- **AuthService** performs login and stores the access token with its locally calculated expiry in session storage.
- **Feature pages** own page state and user interactions; feature data-access services keep endpoint calls separate from view code.
- **HTTP interceptor** attaches the bearer token to same-origin API calls, excluding the login request and unrelated origins.
