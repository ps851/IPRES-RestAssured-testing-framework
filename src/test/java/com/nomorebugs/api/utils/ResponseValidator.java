package com.nomorebugs.api.utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pomocná trieda pre opakujúce sa validácie odpovedí.
 * DRY princíp - validačná logika na jednom mieste.
 */
public class ResponseValidator {

    private static final Logger log = LoggerFactory.getLogger(ResponseValidator.class);

    private ResponseValidator() {}

    /**
     * Overí, že response má očakávaný status kód a nie je prázdny.
     */
    public static void validateStatusCode(Response response, int expectedStatus) {
        log.debug("Overujem status kód: očakávaný={}, skutočný={}",
                expectedStatus, response.statusCode());

        assertThat(response.statusCode())
                .as("HTTP Status kód")
                .isEqualTo(expectedStatus);
    }

    /**
     * Overí Content-Type hlavičku.
     */
    public static void validateContentType(Response response, String expectedContentType) {
        assertThat(response.contentType())
                .as("Content-Type hlavička")
                .containsIgnoringCase(expectedContentType);
    }

    /**
     * Overí, že odpoveď nie je prázdna a má nenulové telo.
     */
    public static void validateNonEmptyBody(Response response) {
        assertThat(response.body().asString())
                .as("Telo odpovede nesmie byť prázdne")
                .isNotBlank();
    }

    /**
     * Overí response time voči maximálnej hodnote v ms.
     */
    public static void validateResponseTime(Response response, long maxMillis) {
        log.debug("Response time: {} ms (max: {} ms)", response.time(), maxMillis);
        assertThat(response.time())
                .as("Response time musí byť pod %d ms", maxMillis)
                .isLessThan(maxMillis);
    }
}
