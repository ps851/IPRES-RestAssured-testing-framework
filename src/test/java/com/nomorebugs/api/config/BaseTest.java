package com.nomorebugs.api.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

/**
 * Base class for all API tests.
 * Initialises REST Assured configuration and shared RequestSpecification once per suite.
 */
public class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    protected static ApiConfig config;
    protected static RequestSpecification requestSpec;

    @BeforeSuite
    public void globalSetup() {
        config = ConfigFactory.create(ApiConfig.class, System.getProperties());

        log.info("=== Test suite initialisation ===");
        log.info("Environment: {}", config.env());
        log.info("Base URL: {}", config.baseUrl());

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured());  // attaches request/response to Allure report

        if (config.enableLogging()) {
            builder.log(LogDetail.ALL);
        }

        requestSpec = builder.build();

        RestAssured.requestSpecification = requestSpec;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
