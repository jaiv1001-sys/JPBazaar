package com.jpbazaar.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpbazaar.dto.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servlet filter enforcing token bucket rate limits across all incoming API requests.
 * Runs after JWT authentication so authenticated users are keyed by username/email,
 * while unauthenticated users are keyed by remote IP address.
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public RateLimitingFilter(
            RateLimiterService rateLimiterService,
            ObjectMapper objectMapper,
            @Value("${app.security.rate-limit.enabled:true}") boolean enabled
    ) {
        this.rateLimiterService = rateLimiterService;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!enabled || isBypassed(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitTier tier = resolveTier(request);
        if (tier == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIdentifier = resolveClientIdentifier(request);
        String bucketKey = clientIdentifier + ":" + tier.name();

        RateLimitResult result = rateLimiterService.tryAcquire(bucketKey, tier);

        response.setHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(result.resetSeconds()));

        if (!result.allowed()) {
            response.setHeader("Retry-After", String.valueOf(result.retryAfterSeconds()));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            log.warn("Rate limit exceeded for key '{}' on path '{}'. Remaining retry wait: {}s",
                    bucketKey, request.getRequestURI(), result.retryAfterSeconds());

            ApiResponse<Void> apiResponse = ApiResponse.error(
                    "Rate limit exceeded. Try again in " + result.retryAfterSeconds() + " seconds."
            );
            objectMapper.writeValue(response.getOutputStream(), apiResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBypassed(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/v1/health")
                || uri.startsWith("/actuator")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.equals("/swagger-ui.html")
                || uri.equals("/favicon.ico")
                || uri.startsWith("/error");
    }

    private RateLimitTier resolveTier(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/auth/")) {
            return RateLimitTier.AUTH;
        }
        if (uri.startsWith("/api/")) {
            return RateLimitTier.GENERAL;
        }
        return null;
    }

    private String resolveClientIdentifier(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "user:" + auth.getName();
        }

        return "ip:" + extractClientIp(request);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            String[] ips = forwarded.split(",");
            return ips[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }

        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }
}
