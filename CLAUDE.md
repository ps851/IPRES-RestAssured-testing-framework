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

This is a REST Assured + TestNG framework targeting the JSONPlaceholder public API (`https://jsonplaceholder.typicode.com`). Tests cover CRUD operations on `/posts` and `/users` endpoints plus authentication pattern examples against `httpbin.org`.

**Package layout** (`src/test/java/com/nomorebugs/api/`):
- `config/` — `ApiConfig` (Owner-based property interface) and `BaseTest` (suite-level `@BeforeSuite` that initialises a shared `RequestSpecification` with the Allure filter)
- `models/` — Jackson-annotated POJOs: `Post`, `User`, `Comment`
- `tests/` — `PostsApiTest` (CRUD + negative), `UsersApiTest` (nested JSON), `AuthExamplesTest` (Basic/Bearer/APIKey against httpbin.org), `SchemaValidationTest` (JSON Schema contracts), `DataDrivenTest` (TestNG `@DataProvider`)
- `utils/` — `TestDataFactory` (UUID-based payloads) and `ResponseValidator` (reusable assertions including response-time threshold)

**Request flow:** `BaseTest.globalSetup()` loads `environment.properties` via the Owner library (system properties override file values), then builds a shared `RequestSpecification` with the Allure REST Assured filter attached. Every test method reuses this spec. `AuthExamplesTest` builds its own separate spec pointing at `httpbin.org`.

**Assertion style:** REST Assured `.then()` chains use Hamcrest matchers for inline HTTP assertions; extracted POJOs are asserted with AssertJ fluent API (`assertThat(...).allSatisfy(...)`).

**JSON Schema contracts:** Schemas live in `src/test/resources/schemas/` and are validated with `matchesJsonSchemaInClasspath()` from the `json-schema-validator` dependency already in `pom.xml`.

**Allure reporting:** Tests are annotated with `@Epic`, `@Feature`, `@Story`, and `@Severity`. The `AllureRestAssured` filter automatically attaches full request/response to every test step. Results land in `target/allure-results`.

**Configuration:** `src/test/resources/environment.properties` is the dev default. `staging.properties` and `prod.properties` are templates activated with `-Denv=staging` / `-Denv=prod`. The TestNG suite file at `src/test/resources/testng.xml` runs all five test classes serially.
