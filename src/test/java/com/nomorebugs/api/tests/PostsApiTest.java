package com.nomorebugs.api.tests;

import com.nomorebugs.api.config.BaseTest;
import com.nomorebugs.api.models.Comment;
import com.nomorebugs.api.models.Post;
import com.nomorebugs.api.utils.ResponseValidator;
import com.nomorebugs.api.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for the /posts endpoint.
 * Covers: GET (list, by ID, filtered, sub-resource), POST, PUT, PATCH, DELETE, negative cases.
 */
@Epic("REST API Testing")
@Feature("Posts API")
public class PostsApiTest extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";

    // =====================================================================
    // GET tests
    // =====================================================================

    @Test(description = "GET all posts - validate list is non-empty and each item is well-formed")
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

        List<Post> posts = response.jsonPath().getList("$", Post.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).allSatisfy(post -> {
            assertThat(post.getId()).isPositive();
            assertThat(post.getTitle()).isNotBlank();
            assertThat(post.getUserId()).isPositive();
        });

        ResponseValidator.validateResponseTime(response, 5000L);
        log.info("Retrieved {} posts", posts.size());
    }

    @Test(description = "GET post by ID - path parameter usage and POJO deserialisation")
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
        log.info("Retrieved post: {}", post);
    }

    @Test(description = "GET posts filtered by userId - query parameter filtering")
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
                .as("All posts must belong to userId=%d", userId)
                .isEqualTo(userId)
        );

        log.info("User {} has {} posts", userId, posts.size());
    }

    @Test(description = "GET comments for a post - sub-resource endpoint with Comment POJO deserialisation")
    @Story("GET /posts/{id}/comments")
    @Severity(SeverityLevel.NORMAL)
    public void getCommentsForPost_shouldReturnComments() {
        int postId = 1;

        List<Comment> comments = given()
                .spec(requestSpec)
                .pathParam("postId", postId)
        .when()
                .get(POSTS_ENDPOINT + "/{postId}/comments")
        .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("postId", everyItem(equalTo(postId)))
                .body("email", everyItem(not(emptyString())))
                .extract().jsonPath().getList("$", Comment.class);

        assertThat(comments).allSatisfy(comment -> {
            assertThat(comment.getEmail()).contains("@");
            assertThat(comment.getPostId()).isEqualTo(postId);
        });
    }

    // =====================================================================
    // POST tests
    // =====================================================================

    @Test(description = "POST - create a new post, verify 201 and response body")
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
        log.info("Created post: {}", createdPost);
    }

    // =====================================================================
    // PUT tests
    // =====================================================================

    @Test(description = "PUT - full resource replacement")
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
    // PATCH tests
    // =====================================================================

    @Test(description = "PATCH - partial resource update, only changed fields sent")
    @Story("PATCH /posts/{id}")
    @Severity(SeverityLevel.NORMAL)
    public void patchPost_shouldReturn200WithPatchedData() {
        int postId = 1;
        String newTitle = "Updated title via PATCH";

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
    // DELETE tests
    // =====================================================================

    @Test(description = "DELETE - remove a post and verify 200 response")
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

        log.info("Post with ID {} deleted", postId);
    }

    // =====================================================================
    // Negative tests
    // =====================================================================

    @Test(description = "GET non-existent post - expect 404 Not Found")
    @Story("Negative Tests")
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
