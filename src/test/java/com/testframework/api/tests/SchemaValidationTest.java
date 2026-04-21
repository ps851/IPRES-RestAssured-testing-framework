package com.testframework.api.tests;

import com.testframework.api.config.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Epic("REST API Testing")
@Feature("JSON Schema Validation")
public class SchemaValidationTest extends BaseTest {

    @Test(description = "GET /posts/{id} - response body matches the Post JSON Schema")
    @Story("Schema /posts")
    @Severity(SeverityLevel.CRITICAL)
    public void getPost_responseShouldMatchJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get("/posts/{id}")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    @Test(description = "GET /users/{id} - response body matches the User JSON Schema")
    @Story("Schema /users")
    @Severity(SeverityLevel.CRITICAL)
    public void getUser_responseShouldMatchJsonSchema() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get("/users/{id}")
        .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
    }

    @Test(description = "GET /posts - first item in list matches Post JSON Schema")
    @Story("Schema /posts")
    @Severity(SeverityLevel.NORMAL)
    public void getPostsList_firstItemShouldMatchSchema() {
        given()
                .spec(requestSpec)
        .when()
                .get("/posts")
        .then()
                .statusCode(200)
                .body("[0]", matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }
}
