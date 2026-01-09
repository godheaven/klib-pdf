# Copilot Context for Kanopus Projects (v.1.1)

## Tech Stack Rules

- Java 17 or 21.
- Spring Boot 3.5.x (WebFlux for API Gateway, MVC for internal services).
- Avoid javax.*; use jakarta.*.
- Logback + SLF4J.
- Prometheus actuator should always be enabled.
- Micrometer for custom metrics.
- Lombok to reduce boilerplate.
- Database: PostgreSQL 15+.
- Use the project's own JPA implementation using klib-data-jdbc.
- All test classes should not use public modifier in class definition.

## Maven Rules

- Parent BOM: `kanopus-boot-parent` for Spring Boot projects.
- Parent BOM: `kanopus-core-parent` for non-Spring Boot libraries.
- Avoid duplicate dependencies.
- Use `maven-surefire-plugin` for unit tests.
- Use `maven-failsafe-plugin` for integration tests.
- Always use version properties (e.g., `${spring-cloud.version}`).

## Testing Rules

- Create unit tests with JUnit 5 and Mockito.
- Integration tests must use the "local" profile and must end with `IT`.
- Use `@SpringBootTest` only when necessary.
- Minimum coverage: 80%.
- The database schema is located under `database/*schema.sql`.
- Initial test data is located under `database/*data.sql`.
- All integration tests must extend `AbstractIT`. In this abstract class the following configuration should exist:
    - `@SpringBootTest(classes = MyApplication.class)`
    - `@ActiveProfiles("local")`
    - `@TestPropertySource(locations = "classpath:application-test.yml")`
    - `@Sql(scripts = {"classpath:integration-schema.sql", "classpath:integration-data.sql"})`

- All unit tests should extend `AbstractUnitTest`.

## Code Style

- Use records for DTOs.
- Expose endpoints with Swagger/OpenAPI.
- Validations with `jakarta.validation`.
- Use `Optional` instead of `null`.
- Avoid complex logic inside lambdas.
- Follow Java naming conventions (camelCase for variables and methods, PascalCase for classes).
- Document public methods with Javadoc.
- Follow SOLID principles.
- Comments, variables, and method names should be in English.
- Avoid using field injection; prefer constructor injection.

## Architecture

- Layered pattern: Controller, Service, Repository.
    - `application/` → Configuration, beans, security
    - `data/` → Repositories, Entities, DAO
    - `service/` → Business logic (`@Service`)
    - `web/` → REST controllers (`@Controller`)

- No business logic in controllers.
- Services should be stateless.
- HTTP integrations should use HttpClient 5.5.1.

## Security

- JWT HS512.
- Rotating refresh token.
- Cookies must be `HttpOnly` + `Secure`.

## Suggestions (Practical recommendations)

1. Centralize test SQL scripts for integration tests in `src/test/resources/` as `integration-schema.sql` and
   `integration-data.sql`. This makes `@Sql` usage declarative and reproducible.

2. Consider using Testcontainers for integration tests that require a database. It improves isolation and CI
   reproducibility without relying on a shared DB instance.

3. Add a Maven profile `local-test` that sets the `local` active profile and binds the `failsafe` execution for
   integration tests; provide a convenience script in `README.md` for `mvn -Plocal-test verify`.

4. Use AssertJ for readable assertions and Mockito for mocking; prefer constructor injection in tests and production
   code.

5. Add a basic CI job that runs unit tests, then integration tests (in a separate step or matrix), and enforces code
   coverage thresholds (e.g., fail build if coverage &lt; 80%).

6. Consider introducing Flyway or Liquibase for schema migrations if the project will evolve; for tests, keep
   lightweight SQL scripts but validate they are aligned with migrations.

7. Keep `AbstractIT` and `AbstractUnitTest` in `src/test/java/.../test/` packages and document their contract (what they
   provide: mocked beans, security disabled, test properties).

8. Add a small `README.md` section describing how to run unit and integration tests locally and in CI, including any
   environment variables required (DB URL, username, password).

9. Where feasible, use parameterized tests for boundary cases and add at least one negative test per critical path (
   e.g., invalid input, missing resource).

10. Regularly run `mvn -DskipTests=false -DskipITs=false verify` locally and in CI to catch configuration regressions
    early.

---

When you ask for further automated changes (e.g., create `AbstractIT`, rename tests, or move SQL files), I can implement
them following these guidelines.
