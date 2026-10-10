# Project engineering guidance



## Java backend



- Apply SOLID principles pragmatically.

- Keep HTTP handling, business use cases, and persistence responsibilities separate.

- Use constructor injection and validate inputs at the system boundary.

- Return DTOs from APIs; do not expose persistence entities directly.

- Add interfaces where they provide a useful boundary. Avoid abstractions without a clear need.

- Enforce authentication and authorization on the server. Never commit credentials or secrets.



## Angular



- Follow the Angular-specific instructions in `frontend/AGENTS.md`.

- Apply the supplied Angular guidelines where compatible with the generated standalone Angular architecture.

- Use clear kebab-case names, strict types, and Angular CLI for generation.

- Keep API communication in focused services and lazy-load feature routes where useful.

- Prefer reusable standalone components over shared NgModules.

- Treat route guards as navigation behavior; backend authorization remains authoritative.

- Keep secrets out of frontend configuration. Browser-delivered environment values are public.



## Change discipline



- Keep changes small and explainable.

- Update the requirements and design artifacts when decisions change.

- Do not add tools or dependencies without a clear use in this assessment.

