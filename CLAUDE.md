# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run all tests
mvn clean test

# Run a single test class
mvn clean test -Dtest=PostsApiTest

# Run a single test method
mvn clean test -Dtest=PostsApiTest#getPostById_shouldReturnCorrectPost

# Run against a different environment
mvn clean test -Denv=staging -Dbase.url=https://staging.api.example.com

# Generate Allure HTML report (after running tests)
mvn allure:report
# Report opens at: target/site/allure-maven-plugin/index.html
```

## Architecture

This is a REST Assured + TestNG framework targeting the JSONPlaceholder public API (`https://jsonplaceholder.typicode.com`). Tests cover CRUD operations on `/posts` and `/users` endpoints.

**Package layout** (`src/test/java/com/nomorebugs/api/`):
- `config/` — `ApiConfig` (Owner-based property interface) and `BaseTest` (suite-level `@BeforeSuite` that initialises a shared `RequestSpecification`)
- `models/` — Jackson-annotated POJOs (`Post`, `User`) used for serialisation/deserialisation
- `tests/` — `PostsApiTest` (10 methods), `UsersApiTest` (4 methods)
- `utils/` — `TestDataFactory` (generates test payloads using UUID-based uniqueness) and `ResponseValidator` (reusable assertion helpers including response-time checks)

**Request flow:** `BaseTest.globalSetup()` loads `environment.properties` via the Owner library (system properties override file values), then builds a `RequestSpecification` with the Allure REST Assured filter attached. Every test method reuses this shared spec.

**Assertion style:** REST Assured `.then()` chains use Hamcrest matchers for inline HTTP assertions; extracted POJOs are asserted with AssertJ fluent API (`assertThat(...).satisfies(...)`).

**Allure reporting:** Tests are annotated with `@Epic`, `@Feature`, `@Story`, and `@Severity`. Results land in `target/allure-results`; the Allure Maven plugin turns them into an HTML report.

**Configuration:** `src/test/resources/environment.properties` holds `base.url`, `request.timeout`, `enable.logging`, and `env`. Override any property at runtime with `-D<key>=<value>`. The TestNG suite file at `src/test/resources/testng.xml` runs both test classes serially (parallel=none).
