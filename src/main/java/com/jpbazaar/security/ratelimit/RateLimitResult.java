package com.jpbazaar.security.ratelimit;

/**
 * Represents the outcome of a rate limit check evaluation.
 * Contains client token availability and HTTP rate limit header values.
 *
 * @param allowed             Whether the request is allowed to proceed
 * @param limit               The maximum capacity permitted in the configured time window
 * @param remaining           The number of tokens/requests remaining in the current window
 * @param retryAfterSeconds   Seconds to wait before retrying (0 when allowed)
 * @param resetSeconds        Seconds until the rate limit bucket is fully replenished
 */
public record RateLimitResult(
        boolean allowed,
        long limit,
        long remaining,
        long retryAfterSeconds,
        long resetSeconds
) {
    public static RateLimitResult allowed(long limit, long remaining, long resetSeconds) {
        return new RateLimitResult(true, limit, remaining, 0, Math.max(1, resetSeconds));
    }

    public static RateLimitResult denied(long limit, long retryAfterSeconds, long resetSeconds) {
        return new RateLimitResult(false, limit, 0, Math.max(1, retryAfterSeconds), Math.max(1, resetSeconds));
    }
}
