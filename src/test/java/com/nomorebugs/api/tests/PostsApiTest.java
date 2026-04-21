package com.nomorebugs.api.tests;

import com.nomorebugs.api.config.BaseTest;
import com.nomorebugs.api.models.Post;
import com.nomorebugs.api.utils.TestDataFactory;
import com.nomorebugs.api.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Testy pre /posts endpoint.
 * Pokrýva: GET všetky, GET by ID, GET s filtrom, POST, PUT, PATCH, DELETE.
 *
 * @author No more bugs solutions, s.r.o.
 */
@Epic("REST API Testing")
@Feature("Posts API")
public class PostsApiTest extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";

    // =====================================================================
    // GET testy
    // =====================================================================

    @Test(description = "GET všetky príspevky - overenie zoznamu")
    @Story("GET /posts")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllPosts_shouldReturn200WithNonEmptyList() {
        Response response = given()
                .spec(requestSpec)
        .when()
                .get(POSTS_ENDPOINT)
        .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].title", not(emptyString()))
                .extract().response();

        // AssertJ overenie na úrovni Java objektov
        List<Post> posts = response.jsonPath().getList("$", Post.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).allSatisfy(post -> {
            assertThat(post.getId()).isPositive();
            assertThat(post.getTitle()).isNotBlank();
            assertThat(post.getUserId()).isPositive();
        });

        ResponseValidator.validateResponseTime(response, 5000L);
        log.info("Načítaných {} príspevkov", posts.size());
    }

    @Test(description = "GET príspevok podľa ID")
    @Story("GET /posts/{id}")
    @Severity(SeverityLevel.CRITICAL)
    public void getPostById_shouldReturnCorrectPost() {
        int postId = 1;

        Post post = given()
                .spec(requestSpec)
                .pathParam("id", postId)
        .when()
                .get(POSTS_ENDPOINT + "/{id}")
        .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", not(emptyString()))
                .body("userId", notNullValue())
                .extract().as(Post.class);

        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post.getTitle()).isNotBlank();
        log.info("Načítaný príspevok: {}", post);
    }

    @Test(description = "GET príspevky filtrované podľa userId")
    @Story("GET /posts?userId=1")
    @Severity(SeverityLevel.NORMAL)
    public void getPostsByUserId_shouldReturnOnlyUserPosts() {
        int userId = 1;

        List<Post> posts = given()
                .spec(requestSpec)
                .queryParam("userId", userId)
        .when()
                .get(POSTS_ENDPOINT)
        .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .extract().jsonPath().getList("$", Post.class);

        assertThat(posts).allSatisfy(post ->
            assertThat(post.getUserId())
                .as("Všetky príspevky musia patriť userId=%d", userId)
                .isEqualTo(userId)
        );

        log.info("Používateľ {} má {} príspevkov", userId, posts.size());
    }

    @Test(description = "GET komentáre pre konkrétny príspevok - sub-resource")
    @Story("GET /posts/{id}/comments")
    @Severity(SeverityLevel.NORMAL)
    public void getCommentsForPost_shouldReturnComments() {
        int postId = 1;

        given()
                .spec(requestSpec)
                .pathParam("postId", postId)
        .when()
                .get(POSTS_ENDPOINT + "/{postId}/comments")
        .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("postId", everyItem(equalTo(postId)))
                .body("email", everyItem(not(emptyString())));
    }

    // =====================================================================
    // POST testy
    // =====================================================================

    @Test(description = "POST - vytvorenie nového príspevku")
    @Story("POST /posts")
    @Severity(SeverityLevel.CRITICAL)
    public void createPost_shouldReturn201WithCreatedPost() {
        Post newPost = TestDataFactory.createValidPost();

        Post createdPost = given()
                .spec(requestSpec)
                .body(newPost)
        .when()
                .post(POSTS_ENDPOINT)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(newPost.getTitle()))
                .body("userId", equalTo(newPost.getUserId()))
                .extract().as(Post.class);

        assertThat(createdPost.getId()).isNotNull().isPositive();
        assertThat(createdPost.getTitle()).isEqualTo(newPost.getTitle());
        log.info("Vytvorený príspevok: {}", createdPost);
    }

    // =====================================================================
    // PUT testy
    // =====================================================================

    @Test(description = "PUT - aktualizácia celého príspevku")
    @Story("PUT /posts/{id}")
    @Severity(SeverityLevel.NORMAL)
    public void updatePost_shouldReturn200WithUpdatedPost() {
        int postId = 1;
        Post updatedPost = TestDataFactory.createValidPost();
        updatedPost.setId(postId);

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(updatedPost)
        .when()
                .put(POSTS_ENDPOINT + "/{id}")
        .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", equalTo(updatedPost.getTitle()));
    }

    // =====================================================================
    // PATCH testy
    // =====================================================================

    @Test(description = "PATCH - čiastočná aktualizácia príspevku")
    @Story("PATCH /posts/{id}")
    @Severity(SeverityLevel.NORMAL)
    public void patchPost_shouldReturn200WithPatchedData() {
        int postId = 1;
        String newTitle = "Aktualizovaný titulok cez PATCH";

        // Posielame len pole, ktoré chceme zmeniť
        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body("{ \"title\": \"" + newTitle + "\" }")
        .when()
                .patch(POSTS_ENDPOINT + "/{id}")
        .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", equalTo(newTitle));
    }

    // =====================================================================
    // DELETE testy
    // =====================================================================

    @Test(description = "DELETE - zmazanie príspevku")
    @Story("DELETE /posts/{id}")
    @Severity(SeverityLevel.NORMAL)
    public void deletePost_shouldReturn200() {
        int postId = 1;

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
        .when()
                .delete(POSTS_ENDPOINT + "/{id}")
        .then()
                .statusCode(200);

        log.info("Príspevok s ID {} bol zmazaný", postId);
    }

    // =====================================================================
    // Negatívne testy
    // =====================================================================

    @Test(description = "GET neexistujúci príspevok - očakávaný 404")
    @Story("Negatívne testy")
    @Severity(SeverityLevel.NORMAL)
    public void getPost_withInvalidId_shouldReturn404() {
        given()
                .spec(requestSpec)
                .pathParam("id", 99999)
        .when()
                .get(POSTS_ENDPOINT + "/{id}")
        .then()
                .statusCode(404);
    }
}
