package com.nomorebugs.api.tests;

import com.nomorebugs.api.config.BaseTest;
import com.nomorebugs.api.models.User;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Testy pre /users endpoint.
 * Demonštruje prácu s komplexnejšími modelmi a vnoreným JSON.
 *
 * @author No more bugs solutions, s.r.o.
 */
@Epic("REST API Testing")
@Feature("Users API")
public class UsersApiTest extends BaseTest {

    private static final String USERS_ENDPOINT = "/users";

    @Test(description = "GET všetci používatelia - overenie štruktúry")
    @Story("GET /users")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllUsers_shouldReturn200WithValidStructure() {
        Response response = given()
                .spec(requestSpec)
        .when()
                .get(USERS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$", hasSize(10))           // JSONPlaceholder má 10 používateľov
                .body("id", everyItem(notNullValue()))
                .body("email", everyItem(containsString("@")))  // email formát
                .body("address.city", everyItem(notNullValue())) // vnorené pole
                .extract().response();

        List<User> users = response.jsonPath().getList("$", User.class);

        assertThat(users).hasSize(10);
        assertThat(users).allSatisfy(user -> {
            assertThat(user.getEmail())
                    .as("Email používateľa %s musí byť platný", user.getName())
                    .contains("@");
            assertThat(user.getName()).isNotBlank();
            assertThat(user.getUsername()).isNotBlank();
        });
    }

    @Test(description = "GET používateľ podľa ID - validácia polí")
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
                // Validácia vnorených objektov
                .body("address", notNullValue())
                .body("address.city", not(emptyString()))
                .body("company", notNullValue())
                .extract().as(User.class);

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isNotBlank();
        log.info("Načítaný používateľ: {}", user);
    }

    @Test(description = "GET príspevky konkrétneho používateľa - vzťah user->posts")
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

    @Test(description = "GET neexistujúci používateľ - 404")
    @Story("Negatívne testy")
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
