package com.nomorebugs.api.config;

import org.aeonbits.owner.Config;

/**
 * API test configuration loaded from environment.properties.
 * Supports override via system properties (e.g. -Denv=staging -Dbase.url=https://...).
 */
@Config.Sources({
    "system:properties",
    "classpath:${env}.properties",
    "classpath:environment.properties"
})
public interface ApiConfig extends Config {

    @Key("base.url")
    @DefaultValue("https://jsonplaceholder.typicode.com")
    String baseUrl();

    @Key("request.timeout")
    @DefaultValue("10000")
    int requestTimeout();

    @Key("enable.logging")
    @DefaultValue("true")
    boolean enableLogging();

    @Key("env")
    @DefaultValue("dev")
    String env();
}
