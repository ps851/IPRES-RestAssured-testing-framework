package com.testframework.api.tests;

import com.testframework.api.config.BaseTest;
import io.qameta.allure.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Demonstrates common authentication patterns used in REST API testing.
 *
 * Uses httpbin.org — a public HTTP inspection service — to verify that
 * credentials and headers are sent correctly. In a real project, replace
 * the base URL and credentials with your actual API endpoints.
 */
@Epic("REST API Testing")
@Feature("Authentication Patterns")
public class AuthExamplesTest extends BaseTest {

    private static final String HTTPBIN_BASE_URL = "https://httpbin.org";
    private RequestSpecification httpBinSpec;

    @BeforeClass
    public void setupAuthSpec() {
        httpBinSpec = new RequestSpecBuilder()
                .setBaseUri(HTTPBIN_BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    // =====================================================================
    // Basic Authentication
    // =====================================================================

    @Test(description = "Basic Auth - valid credentials return 200 with authenticated=true")
    @Story("Basic Auth")
    @Severity(SeverityLevel.CRITICAL)
    public void basicAuth_withValidCredentials_shouldReturn200() {
        String username = "admin";
        String password = "secret123";

        given()
                .spec(httpBinSpec)
                .auth().basic(username, password)
        .when()
                .get("/basic-auth/{user}/{passwd}", username, password)
        .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("user", equalTo(username));
    }

    @Test(description = "Basic Auth - wrong credentials return 401 Unauthorized")
    @Story("Basic Auth")
    @Severity(SeverityLevel.NORMAL)
    public void basicAuth_withWrongCredentials_shouldReturn401() {
        given()
                .spec(httpBinSpec)
                .auth().basic("wrong", "credentials")
        .when()
                .get("/basic-auth/admin/secret123")
        .then()
                .statusCode(401);
    }

    // =====================================================================
    // Bearer Token (OAuth2 / JWT)
    // =====================================================================

    @Test(description = "Bearer Token - Authorization header set via .auth().oauth2()")
    @Story("Bearer Token")
    @Severity(SeverityLevel.CRITICAL)
    public void bearerToken_withValidToken_shouldReturn200() {
        String token = "demo-bearer-token-xyz789";

        given()
                .spec(httpBinSpec)
                .auth().oauth2(token)
        .when()
                .get("/bearer")
        .then()
                .statusCode(200)
                .body("authenticated", equalTo(true))
                .body("token", equalTo(token));
    }

    // =====================================================================
    // API Key Authentication
    // =====================================================================

    @Test(description = "API Key in header - X-API-Key header forwarded to server")
    @Story("API Key Auth")
    @Severity(SeverityLevel.NORMAL)
    public void apiKeyInHeader_shouldBeForwardedCorrectly() {
        String apiKey = "demo-api-key-abc123";

        given()
                .spec(httpBinSpec)
                .header("X-API-Key", apiKey)
        .when()
                .get("/headers")
        .then()
                .statusCode(200)
                .body("headers.'X-Api-Key'", equalTo(apiKey));
    }

    @Test(description = "API Key as query parameter - key included in request args")
    @Story("API Key Auth")
    @Severity(SeverityLevel.NORMAL)
    public void apiKeyAsQueryParam_shouldBeIncludedInRequest() {
        String apiKey = "demo-api-key-abc123";

        given()
                .spec(httpBinSpec)
                .queryParam("api_key", apiKey)
        .when()
                .get("/get")
        .then()
                .statusCode(200)
                .body("args.api_key", equalTo(apiKey));
    }
}
