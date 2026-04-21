package com.testframework.api.utils;

import com.testframework.api.models.Post;

import java.util.UUID;

/**
 * Centralised factory for test data.
 * Single source of truth for all payloads — easy to maintain and extend.
 */
public class TestDataFactory {

    private TestDataFactory() {}

    /** Creates a valid Post payload for create/update tests. */
    public static Post createValidPost() {
        return new Post(
                1,
                "Test title - " + UUID.randomUUID().toString().substring(0, 8),
                "This is a test post body created by an automated test."
        );
    }

    /** Creates a Post assigned to a specific userId for filter tests. */
    public static Post createPostForUser(int userId) {
        return new Post(
                userId,
                "Post for user " + userId + " - " + UUID.randomUUID().toString().substring(0, 8),
                "Post body for user " + userId
        );
    }

    /** Creates a Post with an empty title — for negative/edge-case tests. */
    public static Post createPostWithEmptyTitle() {
        return new Post(1, "", "Post body exists but title is missing.");
    }

    /** Creates a Post with all null fields — for null-handling tests. */
    public static Post createPostWithNullFields() {
        return new Post(null, null, null);
    }
}
