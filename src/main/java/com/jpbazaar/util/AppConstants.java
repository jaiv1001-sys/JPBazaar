package com.jpbazaar.util;

/**
 * Global application constants for JPBazaar.
 */
public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
