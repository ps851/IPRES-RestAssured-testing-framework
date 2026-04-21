# REST Assured API Testing Framework

A professional REST API test automation framework built with **Java**, **REST Assured**, and **TestNG** — demonstrating production-ready patterns for API testing.

> Portfolio and reference project — *Peter Surka*

---

## What this framework demonstrates

| Capability | Implementation |
|---|---|
| HTTP methods | GET, POST, PUT, PATCH, DELETE |
| Authentication | Basic Auth, Bearer Token, API Key (header & query param) |
| Assertions | Hamcrest matchers + AssertJ fluent assertions |
| JSON Schema validation | Contract testing with JSON Schema Draft-7 |
| Data-driven testing | TestNG `@DataProvider` with parameterised test methods |
| POJO serialisation | Jackson with `@JsonIgnoreProperties` |
| Allure reporting | Epic / Feature / Story hierarchy, request + response auto-attached |
| Multi-environment config | dev / staging / prod via `.properties` + system property override |
| Reusable request specs | Shared `RequestSpecification` built once per suite |
| Sub-resource testing | Nested endpoints (e.g. `/posts/{id}/comments`) |
| Negative testing | 4xx error case coverage with dedicated factory methods |
| Performance threshold | Response time assertions via `ResponseValidator` |

---

## Tech stack

| Library | Version | Purpose |
|---|---|---|
| REST Assured | 5.4.0 | HTTP client + fluent DSL |
| TestNG | 7.9.0 | Test runner, `@DataProvider` |
| Jackson | 2.16.1 | JSON ↔ POJO serialisation |
| AssertJ | 3.25.3 | Fluent Java assertions |
| Allure | 2.25.0 | HTML test reports |
| Owner | 1.0.12 | Properties-based configuration |
| Logback | 1.5.3 | Console + rolling file logging |

---

## Project structure

```
src/test/java/com/testframework/api/
├── config/
│   ├── ApiConfig.java              # Owner config interface (base URL, timeout, env)
│   └── BaseTest.java               # @BeforeSuite setup, shared RequestSpecification
├── models/
│   ├── Post.java                   # Jackson POJO
│   ├── User.java                   # Jackson POJO
│   └── Comment.java                # Jackson POJO (sub-resource)
├── tests/
│   ├── PostsApiTest.java           # Full CRUD + negative tests for /posts
│   ├── UsersApiTest.java           # GET tests with nested JSON validation
│   ├── AuthExamplesTest.java       # Basic Auth, Bearer Token, API Key patterns
│   ├── SchemaValidationTest.java   # JSON Schema contract validation
│   └── DataDrivenTest.java         # @DataProvider parameterised tests
└── utils/
    ├── TestDataFactory.java        # Centralised test payload factory (UUID-based)
    └── ResponseValidator.java      # Reusable assertion helpers

src/test/resources/
├── schemas/
│   ├── post-schema.json            # JSON Schema for Post
│   └── user-schema.json            # JSON Schema for User (with nested address/company)
├── environment.properties          # Dev defaults
├── staging.properties              # Staging overrides
├── prod.properties                 # Prod overrides
├── testng.xml                      # Suite definition
└── logback.xml                     # Logging config (console + rolling file)
```

---

## Quick start

```bash
# Run all tests
mvn clean test

# Run a single test class
mvn clean test -Dtest=PostsApiTest

# Run a single test method
mvn clean test -Dtest=PostsApiTest#createPost_shouldReturn201WithCreatedPost

# Run against a different environment
mvn clean test -Denv=staging -Dbase.url=https://staging.api.example.com

# Generate Allure HTML report
mvn allure:report
# Report opens at: target/site/allure-maven-plugin/index.html
```

---

## Test classes

### PostsApiTest
Full CRUD coverage for the `/posts` endpoint:
- `GET /posts` — list validation with AssertJ `allSatisfy`
- `GET /posts/{id}` — path parameter + POJO deserialisation
- `GET /posts?userId=1` — query parameter filtering
- `GET /posts/{id}/comments` — sub-resource with `Comment` POJO
- `POST /posts` — request body serialisation, 201 status
- `PUT /posts/{id}` — full resource replacement
- `PATCH /posts/{id}` — partial update (single field)
- `DELETE /posts/{id}` — deletion
- Negative: non-existent ID → 404

### UsersApiTest
Complex model and nested JSON:
- `GET /users` — nested `address.city` validation, email format check
- `GET /users/{id}` — nested `address` and `company` object assertions
- `GET /users/{id}/posts` — cross-resource relationship
- Negative: non-existent ID → 404

### AuthExamplesTest
Authentication pattern library (against [httpbin.org](https://httpbin.org)):
- Basic Auth — valid credentials → 200 with `authenticated=true`
- Basic Auth — wrong credentials → 401
- Bearer Token — OAuth2/JWT via `.auth().oauth2(token)`
- API Key in `X-API-Key` header
- API Key as query parameter

### SchemaValidationTest
JSON contract testing:
- Single Post response validated against `schemas/post-schema.json`
- Single User response validated against `schemas/user-schema.json`
- First item in list response validated against Post schema

### DataDrivenTest
TestNG `@DataProvider` parameterisation:
- 5 valid post IDs → each returns 200 with correct data
- 3 invalid post IDs → each returns 404
- 3 different POST payloads → each created with 201

---

## Key patterns

### Given-When-Then
```java
given()
    .spec(requestSpec)
    .pathParam("id", 1)
.when()
    .get("/posts/{id}")
.then()
    .statusCode(200)
    .body("title", not(emptyString()));
```

### POJO deserialisation
```java
Post post = given()
    .spec(requestSpec)
    .pathParam("id", 1)
    .when().get("/posts/{id}")
    .then().extract().as(Post.class);
```

### AssertJ fluent assertions
```java
assertThat(posts).allSatisfy(post -> {
    assertThat(post.getId()).isPositive();
    assertThat(post.getTitle()).isNotBlank();
});
```

### JSON Schema validation
```java
.then()
    .statusCode(200)
    .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
```

### Data-driven testing
```java
@DataProvider(name = "validPostIds")
public Object[][] validPostIds() {
    return new Object[][] { {1}, {10}, {25}, {50}, {100} };
}

@Test(dataProvider = "validPostIds")
public void getPostById_withMultipleValidIds_shouldReturn200(int postId) { ... }
```

### Authentication
```java
// Basic Auth
given().auth().basic("username", "password")

// Bearer Token / OAuth2
given().auth().oauth2("your-jwt-token")

// API Key in header
given().header("X-API-Key", "your-api-key")

// API Key as query parameter
given().queryParam("api_key", "your-api-key")
```

---

## Environment configuration

The framework loads configuration from `{env}.properties` in the classpath.
System properties always take priority (useful for CI pipelines).

```bash
mvn clean test -Denv=staging
mvn clean test -Denv=prod
mvn clean test -Dbase.url=https://custom.api.example.com
```

| Property | Default | Description |
|---|---|---|
| `base.url` | `https://jsonplaceholder.typicode.com` | API base URL |
| `request.timeout` | `10000` | Request timeout in ms |
| `enable.logging` | `true` | Log all requests and responses |
| `env` | `dev` | Active environment name |
