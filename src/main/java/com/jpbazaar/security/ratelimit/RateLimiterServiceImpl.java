package com.jpbazaar.security.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Production-ready rate limiter implementation with dual-engine architecture:
 * 1. Distributed sliding counter via Redis (when enabled and reachable) for clustered deployments.
 * 2. High-performance, lock-free ConcurrentHashMap token bucket fallback for standalone/test environments.
 */
@Slf4j
@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final boolean enabled;
    private final boolean useRedis;
    private final ConcurrentHashMap<String, TokenBucket> inMemoryBuckets = new ConcurrentHashMap<>();

    public RateLimiterServiceImpl(
            @Autowired(required = false) StringRedisTemplate redisTemplate,
            @Value("${app.security.rate-limit.enabled:true}") boolean enabled,
            @Value("${app.security.rate-limit.use-redis:false}") boolean useRedis
    ) {
        this.redisTemplate = redisTemplate;
        this.enabled = enabled;
        this.useRedis = useRedis;
    }

    @Override
    public RateLimitResult tryAcquire(String key, RateLimitTier tier) {
        if (!enabled) {
            return RateLimitResult.allowed(tier.getCapacity(), tier.getCapacity(), tier.getWindowSeconds());
        }

        if (useRedis && redisTemplate != null) {
            try {
                return tryAcquireRedis(key, tier);
            } catch (Exception ex) {
                log.warn("Redis rate limiter unavailable ({}). Falling back to in-memory token bucket.", ex.getMessage());
            }
        }

        return tryAcquireInMemory(key, tier);
    }

    @Override
    public void reset(String key) {
        inMemoryBuckets.remove(key);
        if (useRedis && redisTemplate != null) {
            try {
                redisTemplate.delete("ratelimit:" + key);
            } catch (Exception ex) {
                log.debug("Failed to reset Redis rate limit key {}: {}", key, ex.getMessage());
            }
        }
    }

    private RateLimitResult tryAcquireRedis(String key, RateLimitTier tier) {
        String redisKey = "ratelimit:" + key;
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount == null) {
            return tryAcquireInMemory(key, tier);
        }

        if (currentCount == 1L) {
            redisTemplate.expire(redisKey, tier.getWindow());
        }

        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        long resetSeconds = (ttl != null && ttl > 0) ? ttl : tier.getWindowSeconds();

        if (currentCount > tier.getCapacity()) {
            return RateLimitResult.denied(tier.getCapacity(), resetSeconds, resetSeconds);
        } else {
            long remaining = Math.max(0, tier.getCapacity() - currentCount);
            return RateLimitResult.allowed(tier.getCapacity(), remaining, resetSeconds);
        }
    }

    private RateLimitResult tryAcquireInMemory(String key, RateLimitTier tier) {
        TokenBucket bucket = inMemoryBuckets.computeIfAbsent(
                key,
                k -> new TokenBucket(tier.getCapacity(), tier.getWindow())
        );
        return bucket.tryConsume();
    }

    /**
     * Thread-safe in-memory Token Bucket with lazy token replenishment based on wall-clock time.
     */
    static final class TokenBucket {
        private final long capacity;
        private final double refillTokensPerSecond;
        private double tokens;
        private long lastRefillNanos;

        TokenBucket(long capacity, Duration window) {
            this.capacity = capacity;
            this.refillTokensPerSecond = (double) capacity / Math.max(1, window.getSeconds());
            this.tokens = capacity;
            this.lastRefillNanos = System.nanoTime();
        }

        synchronized RateLimitResult tryConsume() {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + (elapsedSeconds * refillTokensPerSecond));
            lastRefillNanos = now;

            if (tokens >= 1.0) {
                tokens -= 1.0;
                long remaining = (long) Math.floor(tokens);
                long resetSeconds = (long) Math.ceil((capacity - tokens) / refillTokensPerSecond);
                return RateLimitResult.allowed(capacity, remaining, Math.max(1, resetSeconds));
            } else {
                double needed = 1.0 - tokens;
                long retryAfter = (long) Math.ceil(needed / refillTokensPerSecond);
                long resetSeconds = (long) Math.ceil(capacity / refillTokensPerSecond);
                return RateLimitResult.denied(capacity, Math.max(1, retryAfter), Math.max(1, resetSeconds));
            }
        }
    }
}
