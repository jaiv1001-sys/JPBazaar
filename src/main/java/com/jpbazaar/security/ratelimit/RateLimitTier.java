package com.jpbazaar.security.ratelimit;

import lombok.Getter;

import java.time.Duration;

/**
 * Defines rate limiting tiers across different endpoint classifications in JPBazaar.
 */
@Getter
public enum RateLimitTier {

    /**
     * Strict tier for authentication and credential verification endpoints (/api/auth/**).
     * Defends against brute-force attacks, credential stuffing, and account enumeration.
     */
    AUTH(10, Duration.ofMinutes(1)),

    /**
     * Standard tier for general API operations (/api/**).
     * Defends against denial of service, resource scraping, and abusive traffic.
     */
    GENERAL(100, Duration.ofMinutes(1));

    private final long capacity;
    private final Duration window;

    RateLimitTier(long capacity, Duration window) {
        this.capacity = capacity;
        this.window = window;
    }

    public long getWindowSeconds() {
        return window.getSeconds();
    }
}
