package com.nomorebugs.api.utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reusable response assertion helpers.
 * Keeps validation logic in one place (DRY principle).
 */
public class ResponseValidator {

    private static final Logger log = LoggerFactory.getLogger(ResponseValidator.class);

    private ResponseValidator() {}

    /** Asserts the response has the expected HTTP status code. */
    public static void validateStatusCode(Response response, int expectedStatus) {
        log.debug("Validating status code: expected={}, actual={}", expectedStatus, response.statusCode());
        assertThat(response.statusCode())
                .as("HTTP status code")
                .isEqualTo(expectedStatus);
    }

    /** Asserts the Content-Type header contains the expected value (case-insensitive). */
    public static void validateContentType(Response response, String expectedContentType) {
        assertThat(response.contentType())
                .as("Content-Type header")
                .containsIgnoringCase(expectedContentType);
    }

    /** Asserts the response body is not blank. */
    public static void validateNonEmptyBody(Response response) {
        assertThat(response.body().asString())
                .as("Response body must not be empty")
                .isNotBlank();
    }

    /** Asserts the response time is below the given threshold in milliseconds. */
    public static void validateResponseTime(Response response, long maxMillis) {
        log.debug("Response time: {} ms (threshold: {} ms)", response.time(), maxMillis);
        assertThat(response.time())
                .as("Response time must be below %d ms", maxMillis)
                .isLessThan(maxMillis);
    }
}
