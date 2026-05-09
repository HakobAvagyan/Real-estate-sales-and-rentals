# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

Maven multi-module project. Wrappers live under `app/` and `rest/` (not at the root).

```bash
# Build everything from the repo root (uses system Maven — root has no mvnw)
mvn clean install -DskipTests

# Run the server-rendered Thymeleaf app (port 8080, default)
cd app && ./mvnw spring-boot:run

# Run the JSON REST API (port 8081, JWT-secured)
cd rest && ./mvnw spring-boot:run

# Spin up MySQL (the schema `rent_sell`, user `admin`/`1234`)
docker compose up -d    # note: `doscker-compos.yml` in root is a typo duplicate, ignore
```

Per-developer config is picked up via Spring profile: copy/edit `app/src/main/resources/application-<name>.yaml` (see `application-sam.yaml`) and run with `-Dspring-boot.run.profiles=<name>`. The `rest` module expects a `JWT_SECRET` env var.

## Tests

```bash
# Run all tests in a module
cd common && mvn test
cd app    && ./mvnw test        # uses H2 in-memory via application-test.yaml
cd rest   && ./mvnw test

# Single test class / method
mvn -pl common test -Dtest=PropertyServiceImplTest
mvn -pl common test -Dtest=PropertyServiceImplTest#methodName
```

`app` tests disable Liquibase and point at H2 with `MODE=MySQL` (`app/src/test/resources/application-test.yaml`). When writing new integration tests in `app`, activate the `test` profile so that config applies.

## Architecture

Four Maven modules with a strict dependency direction — `persistence` → `common` → (`app`, `rest`). `app` and `rest` are two **separate Spring Boot runnable applications** that share the same domain model, services, and DB schema:

- **`persistence`** (JAR, Java 21) — JPA entities (`org.example.model`), repositories (`org.example.repository`), DTOs (`org.example.dto.*`). No Spring Boot, just `spring-data-jpa` + validation. This is the single source of truth for the data model; both apps `@EntityScan("org.example.model")` and `@EnableJpaRepositories("org.example.repository")`.
- **`common`** (JAR, Java 21) — All business logic: service interfaces + `impl/`, MapStruct mappers (`org.example.mapper.*`), JWT issuing/parsing (`org.example.security.jwt.JwtService`), WebSocket/STOMP chat infrastructure (`org.example.chat`), email (`SendEmailServiceImpl`), shared exception types (`org.example.exception`). Services here are annotated `@Service` and auto-wired into both apps.
- **`app`** (Spring Boot 4.0.3, Java 21) — Thymeleaf server-rendered MVC at port 8080. Session-based auth via Spring Security form login (`WebSecurityConfig`), role-gated URL patterns (`ADMIN`/`MANAGER`/`USER`). Owns the Liquibase changelogs under `src/main/resources/db/changelog/` loaded via `db/master.xml` (`<includeAll>` — file **ordering matters**, they're named `change-1.0-…` through `change-2.2-…`).
- **`rest`** (Spring Boot 4.0.3, Java **24 with `--enable-preview`**) — Stateless JSON API at port 8081. JWT auth via `JwtAuthenticationFilter` (in `rest.security`), `SessionCreationPolicy.STATELESS`. Swagger UI at `/swagger-ui/**`. Note: `rest` does **not** ship its own Liquibase changelogs — it relies on `app` having already migrated the DB, or on running against the same MySQL instance after `app` starts.

### Cross-cutting pieces to know

- **Chat** uses STOMP-over-WebSocket. `common/chat/` defines the config, handshake handler, and `ChatStompController`. Each app provides its own `StompUserPrincipalProvider` (`AppStompPrincipalProvider` in `app`, `RestStompPrincipalProvider` in `rest`) because principal extraction differs between session-auth and JWT-auth. Endpoint: `/ws-chat/**`.
- **Both apps `@ComponentScan("org.example")`** so any `@Service`/`@Component` in `common` is picked up by both. Keep package base `org.example.*` when adding shared beans.
- **Roles** live in `org.example.model.enums` and are referenced as `hasAuthority("ADMIN" | "MANAGER" | "USER")` — not `hasRole`, since there is no `ROLE_` prefix in the DB.
- **File uploads** are written to an absolute path read from `system.upload.images.directory.path`. Per-dev overrides live in the `application-<name>.yaml` profile. The default `rest/src/main/resources/application.yaml` still has a Windows path — override it locally.
- **MapStruct + Lombok** are both active. The compiler plugin configures `lombok-mapstruct-binding` so Lombok-generated getters/setters are visible to MapStruct. If a mapper compiles but produces nulls, check that the Lombok processor path is still declared in the module's `pom.xml`.

### Adding a new feature end-to-end

Typical path: entity in `persistence/model` → repository in `persistence/repository` → DTO in `persistence/dto/<area>` → mapper in `common/mapper/<area>` → service interface in `common/service` + impl in `common/service/impl` → controller in **both** `app/controller/...` (Thymeleaf, returns view name) **and** `rest/controller/...` (`@RestController`, returns DTO). Then add a Liquibase changelog as `app/src/main/resources/db/changelog/change-<next-version>-<desc>.xml` and reference it implicitly via `<includeAll>` — no master edit needed, but filename ordering determines apply order.