package com.jpbazaar.exception;

import lombok.Getter;

/**
 * Thrown when an incoming request exceeds the configured rate limit capacity.
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
