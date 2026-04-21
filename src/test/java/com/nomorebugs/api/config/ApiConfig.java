package com.nomorebugs.api.config;

import org.aeonbits.owner.Config;

/**
 * Konfigurácia API testov načítavaná z environment.properties.
 * Podporuje prepisovanie cez system properties (napr. -Denv=staging).
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
