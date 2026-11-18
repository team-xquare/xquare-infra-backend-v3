# syntax=docker/dockerfile:1.10
FROM eclipse-temurin:17-jdk-alpine AS deps
WORKDIR /app
RUN apk add --no-cache bash findutils gcompat
COPY ./build.gradle* ./settings.gradle* ./gradle.properties* ./gradlew* ./
RUN --mount=type=bind,source=gradle,target=/tmp/gradle cp -r /tmp/gradle . 2>/dev/null || mkdir -p gradle
RUN chmod +x gradlew 2>/dev/null || chmod +x ./gradlew 2>/dev/null || true
RUN --mount=type=cache,target=/root/.gradle,sharing=locked ./gradlew dependencies --no-daemon 2>/dev/null || true
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
RUN apk add --no-cache bash findutils gcompat
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.caching=true -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
COPY ./build.gradle* ./settings.gradle* ./gradle.properties* ./gradlew* ./
RUN --mount=type=bind,source=gradle,target=/tmp/gradle cp -r /tmp/gradle . 2>/dev/null || mkdir -p gradle
COPY ./ .
RUN --mount=type=cache,target=/root/.gradle,sharing=locked chmod +x gradlew 2>/dev/null || true && ./gradlew bootJar -x test --no-daemon && find /app -type f -name '*-plain.jar' -delete
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache tzdata curl ca-certificates && addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser && rm -rf /var/cache/apk/*
WORKDIR /app
ENV TZ=Asia/Seoul JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom" JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError"
COPY --from=build --chown=appuser:appgroup /app/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

