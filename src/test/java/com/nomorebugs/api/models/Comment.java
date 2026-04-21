package com.nomorebugs.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for the JSONPlaceholder /posts/{id}/comments sub-resource.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("postId")
    private Integer postId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("body")
    private String body;

    public Comment() {}

    public Integer getId() { return id; }
    public Integer getPostId() { return postId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBody() { return body; }

    public void setId(Integer id) { this.id = id; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", postId=" + postId + ", email='" + email + "'}";
    }
}
