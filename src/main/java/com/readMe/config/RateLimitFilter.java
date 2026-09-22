package com.readMe.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Limits repeated hits to login/register per client IP — without this, a
// public site is an open invitation for bots to spam-register accounts or
// brute-force passwords, since neither endpoint had any throttling before.
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        // 5 attempts per minute per IP, refilling gradually — generous
        // enough for a real user who mistypes a password twice, tight
        // enough to make brute-forcing impractical.
        Bandwidth limit = Bandwidth.builder()
                .capacity(15)
                .refillGreedy(15, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isAuthEndpoint = path.equals("/api/auth/login") || path.equals("/api/auth/register");

        if (isAuthEndpoint) {
            String clientIp = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(clientIp, k -> newBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Too many attempts. Please wait a moment and try again.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}