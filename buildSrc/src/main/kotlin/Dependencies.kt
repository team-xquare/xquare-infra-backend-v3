object Dependencies {
    object SpringBoot {
        const val VERSION = "3.5.7"
        const val DATA_JPA = "org.springframework.boot:spring-boot-starter-data-jpa:$VERSION"
        const val WEB = "org.springframework.boot:spring-boot-starter-web:$VERSION"
        const val SECURITY = "org.springframework.boot:spring-boot-starter-security:$VERSION"
        const val VALIDATION = "org.springframework.boot:spring-boot-starter-validation:$VERSION"
        const val WEBFLUX = "org.springframework.boot:spring-boot-starter-webflux:$VERSION"
        const val TEST = "org.springframework.boot:spring-boot-starter-test:$VERSION"
        const val DATA_REDIS = "org.springframework.boot:spring-boot-starter-data-redis:$VERSION"
        const val MAIL = "org.springframework.boot:spring-boot-starter-mail:$VERSION"
        const val THYMELEAF = "org.springframework.boot:spring-boot-starter-thymeleaf:$VERSION"
    }

    object SpringCloud {
        private const val FEIGN_VERSION = "4.3.0"
        private const val FEIGN_OKHTTP_VERSION = "13.6"
        const val FEIGN = "org.springframework.cloud:spring-cloud-starter-openfeign:$FEIGN_VERSION"
        const val FEIGN_OKHTTP = "io.github.openfeign:feign-okhttp:$FEIGN_OKHTTP_VERSION"
    }

    object SpringDoc {
        private const val OPENAPI_VERSION = "2.8.13"
        const val OPENAPI = "org.springdoc:springdoc-openapi-starter-webmvc-ui:$OPENAPI_VERSION"
    }

    object Kotlin {
        private const val VERSION = "1.9.25"
        private const val JACKSON_VERSION = "2.19.2"
        private const val COROUTINE_VERSION = "1.8.1"
        const val REFLECT = "org.jetbrains.kotlin:kotlin-reflect:$VERSION"
        const val JACKSON = "com.fasterxml.jackson.module:jackson-module-kotlin:$JACKSON_VERSION"
        const val JACKSON_DATAFORMAT_YAML = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$JACKSON_VERSION"
        const val TEST_JUNIT5 = "org.jetbrains.kotlin:kotlin-test-junit5:$VERSION"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINE_VERSION"
        const val COROUTINES_REACTOR = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$COROUTINE_VERSION"

    }

    object Database {
        private const val MYSQL_CONNECTOR_VERSION = "9.5.0"
        const val MYSQL_CONNECTOR = "com.mysql:mysql-connector-j:$MYSQL_CONNECTOR_VERSION"
    }

    object JWT {
        private const val VERSION = "0.13.0"
        const val API = "io.jsonwebtoken:jjwt-api:$VERSION"
        const val IMPL = "io.jsonwebtoken:jjwt-impl:$VERSION"
        const val JACKSON = "io.jsonwebtoken:jjwt-jackson:$VERSION"
    }

    object Test {
        private const val JUNIT_PLATFORM_LAUNCHER_VERSION = "6.0.1"
        const val JUNIT_PLATFORM_LAUNCHER = "org.junit.platform:junit-platform-launcher:$JUNIT_PLATFORM_LAUNCHER_VERSION"
    }

    object HTTPClient {
        private const val OKHTTP_VERSION = "4.12.0"
        const val OKHTTP = "com.squareup.okhttp3:okhttp:$OKHTTP_VERSION"
        const val OKHTTP_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:$OKHTTP_VERSION"
    }
}