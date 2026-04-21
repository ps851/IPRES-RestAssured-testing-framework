package com.testframework.api.tests;

import com.testframework.api.config.BaseTest;
import com.testframework.api.models.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for the /users endpoint.
 * Demonstrates nested JSON validation, complex POJO models, and sub-resource relationships.
 */
@Epic("REST API Testing")
@Feature("Users API")
public class UsersApiTest extends BaseTest {

    private static final String USERS_ENDPOINT = "/users";

    @Test(description = "GET all users - validate list size, email format, and nested address field")
    @Story("GET /users")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllUsers_shouldReturn200WithValidStructure() {
        Response response = given()
                .spec(requestSpec)
        .when()
                .get(USERS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$", hasSize(10))
                .body("id", everyItem(notNullValue()))
                .body("email", everyItem(containsString("@")))
                .body("address.city", everyItem(notNullValue()))  // nested field validation
                .extract().response();

        List<User> users = response.jsonPath().getList("$", User.class);

        assertThat(users).hasSize(10);
        assertThat(users).allSatisfy(user -> {
            assertThat(user.getEmail())
                    .as("Email of user '%s' must be valid", user.getName())
                    .contains("@");
            assertThat(user.getName()).isNotBlank();
            assertThat(user.getUsername()).isNotBlank();
        });
    }

    @Test(description = "GET user by ID - nested object validation (address, company)")
    @Story("GET /users/{id}")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserById_shouldReturnCorrectUser() {
        int userId = 1;

        User user = given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(USERS_ENDPOINT + "/{id}")
        .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", not(emptyString()))
                .body("email", containsString("@"))
                .body("address", notNullValue())
                .body("address.city", not(emptyString()))
                .body("company", notNullValue())
                .extract().as(User.class);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isNotBlank();
        log.info("Retrieved user: {}", user);
    }

    @Test(description = "GET posts for a specific user - cross-resource relationship")
    @Story("GET /users/{id}/posts")
    @Severity(SeverityLevel.NORMAL)
    public void getUserPosts_shouldReturnPostsForUser() {
        int userId = 1;

        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get(USERS_ENDPOINT + "/{id}/posts")
        .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test(description = "GET non-existent user - expect 404 Not Found")
    @Story("Negative Tests")
    @Severity(SeverityLevel.NORMAL)
    public void getUser_withInvalidId_shouldReturn404() {
        given()
                .spec(requestSpec)
                .pathParam("id", 9999)
        .when()
                .get(USERS_ENDPOINT + "/{id}")
        .then()
                .statusCode(404);
    }
}
