# ==============================================================================
# Phase 21: JPBazaar Multi-Stage Production Dockerfile
# Stage 1: Build & Layer Extraction (Eclipse Temurin JDK 21 Alpine)
# Stage 2: Minimal Distroless/Alpine Runtime (Eclipse Temurin JRE 21 Alpine)
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. BUILD STAGE
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and POM first for Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copy application source code and SQL schema
COPY src/ src/
COPY sql/ sql/

# Build production executable JAR (skipping unit tests as verified in CI)
RUN ./mvnw clean package -DskipTests -B

# Extract Spring Boot 3 layered JAR for optimal Docker layer caching
RUN java -Djarmode=layertools -jar target/jpbazaar-0.0.1-SNAPSHOT.jar extract --destination target/extracted

# ------------------------------------------------------------------------------
# 2. RUNTIME STAGE
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runner

LABEL maintainer="JPBazaar Engineering <engineering@jpbazaar.com>" \
      description="Production container image for JPBazaar E-Commerce API" \
      version="1.0.0"

# Install curl for container healthcheck and tzdata for accurate UTC timestamps
RUN apk add --no-cache curl tzdata

# Security Hardening: Create non-root system user and group (UID/GID 1001)
RUN addgroup -S appgroup -g 1001 && \
    adduser -S appuser -u 1001 -G appgroup

WORKDIR /app

# Create persistent image upload and temp directories with non-root ownership
RUN mkdir -p /app/uploads /app/logs && \
    chown -R appuser:appgroup /app

# Copy layered Spring Boot components from builder stage with strict non-root ownership
# Layers ordered from least frequently changed (dependencies) to most frequently changed (application)
COPY --from=builder --chown=appuser:appgroup /build/target/extracted/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/target/extracted/application/ ./

# Switch to unprivileged user
USER appuser

# Containerized JVM Optimization:
# - UseContainerSupport: Auto-detects cgroup CPU & memory limits
# - MaxRAMPercentage: Allocates up to 75% of container RAM to JVM heap
# - InitialRAMPercentage: Pre-allocates 50% RAM to prevent early heap resizing pauses
# - egd=file:/dev/./urandom: Fast non-blocking cryptographic entropy for JWT signing
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# Expose standard application HTTP port
EXPOSE 8080

# Container Healthcheck verifying application availability and database connectivity
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Launch application using Spring Boot 3 Layered JarLauncher
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
