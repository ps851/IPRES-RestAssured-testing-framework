package com.nomorebugs.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model pre JSONPlaceholder /posts endpoint.
 * Používa Jackson anotácie pre JSON serializáciu/deserializáciu.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    // Default constructor pre Jackson
    public Post() {}

    // Builder-style constructor pre tvorbu test dát
    public Post(Integer userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // Gettery
    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }

    // Settery
    public void setId(Integer id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", userId=" + userId +
               ", title='" + title + "'}";
    }
}
