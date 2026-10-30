package com.jpbazaar.security.ratelimit;

/**
 * Service contract for rate limiting evaluation and token bucket replenishment.
 */
public interface RateLimiterService {

    /**
     * Attempts to acquire an execution permit for the given client identifier and tier.
     *
     * @param key  Unique rate limit identifier (e.g., "ip:192.168.1.1" or "user:admin@jpbazaar.com")
     * @param tier Rate limit capacity tier defining window and burst allowance
     * @return Result containing allowance verdict, remaining quota, and reset timings
     */
    RateLimitResult tryAcquire(String key, RateLimitTier tier);

    /**
     * Resets rate limit quota for the given key (e.g., for testing or manual administrative unlock).
     *
     * @param key Unique rate limit identifier
     */
    void reset(String key);
}
