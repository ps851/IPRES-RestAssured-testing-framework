package com.testframework.api.tests;

import com.testframework.api.config.BaseTest;
import com.testframework.api.models.Post;
import com.testframework.api.utils.TestDataFactory;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("REST API Testing")
@Feature("Data-Driven Tests")
public class DataDrivenTest extends BaseTest {

    @DataProvider(name = "validPostIds")
    public Object[][] validPostIds() {
        return new Object[][] { {1}, {10}, {25}, {50}, {100} };
    }

    @DataProvider(name = "invalidPostIds")
    public Object[][] invalidPostIds() {
        return new Object[][] { {9999}, {99999}, {999999} };
    }

    @DataProvider(name = "postPayloads")
    public Object[][] postPayloads() {
        return new Object[][] {
            { TestDataFactory.createValidPost() },
            { TestDataFactory.createPostForUser(2) },
            { TestDataFactory.createPostForUser(5) }
        };
    }

    @Test(dataProvider = "validPostIds",
          description = "GET post by ID - runs for each valid ID supplied by DataProvider")
    @Story("GET /posts/{id}")
    @Severity(SeverityLevel.NORMAL)
    public void getPostById_withMultipleValidIds_shouldReturn200(int postId) {
        Post post = given()
                .spec(requestSpec)
                .pathParam("id", postId)
        .when()
                .get("/posts/{id}")
        .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .extract().as(Post.class);

        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post.getTitle()).isNotBlank();
        log.info("Verified post ID {}: '{}'", postId, post.getTitle());
    }

    @Test(dataProvider = "invalidPostIds",
          description = "GET post by invalid ID - each out-of-range ID should return 404")
    @Story("GET /posts/{id}")
    @Severity(SeverityLevel.NORMAL)
    public void getPostById_withInvalidIds_shouldReturn404(int postId) {
        given()
                .spec(requestSpec)
                .pathParam("id", postId)
        .when()
                .get("/posts/{id}")
        .then()
                .statusCode(404);

        log.info("Confirmed 404 for post ID {}", postId);
    }

    @Test(dataProvider = "postPayloads",
          description = "POST with different payloads - each payload should be created with 201")
    @Story("POST /posts")
    @Severity(SeverityLevel.NORMAL)
    public void createPost_withDifferentPayloads_shouldReturn201(Post payload) {
        Post created = given()
                .spec(requestSpec)
                .body(payload)
        .when()
                .post("/posts")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(payload.getTitle()))
                .extract().as(Post.class);

        assertThat(created.getId()).isPositive();
        assertThat(created.getTitle()).isEqualTo(payload.getTitle());
        log.info("Created post for userId={}: assigned ID={}", payload.getUserId(), created.getId());
    }
}
