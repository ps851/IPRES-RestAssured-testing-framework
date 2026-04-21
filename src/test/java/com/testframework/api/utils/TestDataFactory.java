package com.testframework.api.utils;

import com.testframework.api.models.Post;

import java.util.UUID;

public class TestDataFactory {

    private TestDataFactory() {}

    public static Post createValidPost() {
        return new Post(
                1,
                "Test title - " + UUID.randomUUID().toString().substring(0, 8),
                "This is a test post body created by an automated test."
        );
    }

    public static Post createPostForUser(int userId) {
        return new Post(
                userId,
                "Post for user " + userId + " - " + UUID.randomUUID().toString().substring(0, 8),
                "Post body for user " + userId
        );
    }

    public static Post createPostWithEmptyTitle() {
        return new Post(1, "", "Post body exists but title is missing.");
    }

    public static Post createPostWithNullFields() {
        return new Post(null, null, null);
    }
}
