# REST Assured API Testing Demo

Vzorové repository pre testovanie REST API pomocou knižnice **REST Assured** v Jave.  
Vytvorené ako portfóliový projekt — *No more bugs solutions, s.r.o.*

---

## Technológie

| Knižnica | Verzia | Účel |
|---|---|---|
| REST Assured | 5.4.0 | HTTP volania + fluent assertions |
| TestNG | 7.9.0 | Test framework |
| Jackson | 2.16.1 | JSON ↔ POJO serializácia |
| AssertJ | 3.25.3 | Fluent Java assertions |
| Allure | 2.25.0 | HTML test reporty |
| Owner | 1.0.12 | Konfigurácia z .properties |
| Logback | 1.5.3 | Logovanie |

---

## Štruktúra projektu

```
restassured-demo/
├── pom.xml
└── src/test/
    ├── java/com/nomorebugs/api/
    │   ├── config/
    │   │   ├── ApiConfig.java        # Owner konfigurácia
    │   │   └── BaseTest.java         # Inicializácia REST Assured
    │   ├── models/
    │   │   ├── Post.java             # POJO model
    │   │   └── User.java             # POJO model
    │   ├── tests/
    │   │   ├── PostsApiTest.java     # GET, POST, PUT, PATCH, DELETE, negatívne
    │   │   └── UsersApiTest.java     # GET zoznam, GET by ID, sub-resource
    │   └── utils/
    │       ├── TestDataFactory.java  # Generovanie testovacích dát
    │       └── ResponseValidator.java # Opakujúce sa validácie
    └── resources/
        ├── environment.properties    # Konfigurácia (URL, timeout...)
        ├── testng.xml                # TestNG suite definícia
        └── logback.xml               # Logovanie
```

---

## Spustenie testov

### Všetky testy
```bash
mvn clean test
```

### Testy s iným prostredím
```bash
mvn clean test -Denv=staging -Dbase.url=https://staging.api.example.com
```

### Konkrétna testovacia trieda
```bash
mvn clean test -Dtest=PostsApiTest
```

### Generovanie Allure reportu
```bash
mvn allure:report
# Report sa vygeneruje do: target/site/allure-maven-plugin/
```

---

## Pokryté HTTP metódy

- **GET** — zoznam, by ID, query parametre, path parametre, sub-resources
- **POST** — vytvorenie záznamu s body
- **PUT** — kompletná aktualizácia
- **PATCH** — čiastočná aktualizácia
- **DELETE** — zmazanie záznamu

---

## Testovacia API

Projekt testuje verejné API **[JSONPlaceholder](https://jsonplaceholder.typicode.com)** — ideálne pre demo a portfólio.

- `GET /posts` — zoznam príspevkov
- `GET /posts/{id}` — príspevok podľa ID
- `GET /posts?userId=1` — filtrovanie
- `POST /posts` — vytvorenie
- `PUT /posts/{id}` — aktualizácia
- `PATCH /posts/{id}` — čiastočná aktualizácia
- `DELETE /posts/{id}` — zmazanie
- `GET /users` — zoznam používateľov
- `GET /users/{id}/posts` — príspevky používateľa

---

## Kľúčové vzory

### Given-When-Then štruktúra
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

### Deserializácia do POJO
```java
Post post = given()...extract().as(Post.class);
```

### AssertJ fluent assertions
```java
assertThat(posts).allSatisfy(post -> {
    assertThat(post.getId()).isPositive();
    assertThat(post.getTitle()).isNotBlank();
});
```
