package com.nomorebugs.api.utils;

import com.nomorebugs.api.models.Post;

import java.util.UUID;

/**
 * Továrňa na generovanie testovacích dát.
 * Centralizované miesto pre všetky test data - ľahká údržba.
 */
public class TestDataFactory {

    private TestDataFactory() {
        // Utility class - zabrání inštanciovaniu
    }

    /**
     * Vytvorí validný Post objekt pre create testy.
     */
    public static Post createValidPost() {
        return new Post(
                1,
                "Testovací nadpis - " + UUID.randomUUID().toString().substring(0, 8),
                "Toto je testovací obsah príspevku vytvorený automatizovaným testom."
        );
    }

    /**
     * Vytvorí Post s konkrétnym userId pre filter testy.
     */
    public static Post createPostForUser(int userId) {
        return new Post(
                userId,
                "Príspevok pre používateľa " + userId,
                "Obsah príspevku pre používateľa č. " + userId
        );
    }

    /**
     * Vytvorí Post s prázdnym titulkom - pre negatívne testy.
     */
    public static Post createPostWithEmptyTitle() {
        return new Post(1, "", "Telo príspevku existuje, titulok chýba.");
    }

    /**
     * Vytvorí Post s null hodnotami - pre negatívne testy.
     */
    public static Post createPostWithNullFields() {
        return new Post(null, null, null);
    }
}
