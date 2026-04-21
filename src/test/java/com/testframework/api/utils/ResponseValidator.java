package com.testframework.api.utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseValidator {

    private static final Logger log = LoggerFactory.getLogger(ResponseValidator.class);

    private ResponseValidator() {}

    public static void validateStatusCode(Response response, int expectedStatus) {
        log.debug("Validating status code: expected={}, actual={}", expectedStatus, response.statusCode());
        assertThat(response.statusCode())
                .as("HTTP status code")
                .isEqualTo(expectedStatus);
    }

    public static void validateContentType(Response response, String expectedContentType) {
        assertThat(response.contentType())
                .as("Content-Type header")
                .containsIgnoringCase(expectedContentType);
    }

    public static void validateNonEmptyBody(Response response) {
        assertThat(response.body().asString())
                .as("Response body must not be empty")
                .isNotBlank();
    }

    public static void validateResponseTime(Response response, long maxMillis) {
        log.debug("Response time: {} ms (threshold: {} ms)", response.time(), maxMillis);
        assertThat(response.time())
                .as("Response time must be below %d ms", maxMillis)
                .isLessThan(maxMillis);
    }
}
