object Dependencies {
    object SpringBoot {
        const val DATA_JPA = "org.springframework.boot:spring-boot-starter-data-jpa"
        const val WEB = "org.springframework.boot:spring-boot-starter-web"
        const val SECURITY = "org.springframework.boot:spring-boot-starter-security"
        const val VALIDATION = "org.springframework.boot:spring-boot-starter-validation"
        const val WEBFLUX = "org.springframework.boot:spring-boot-starter-webflux"
        const val TEST = "org.springframework.boot:spring-boot-starter-test"
    }

    object SpringCloud {
        private const val FEIGN_VERSION = "4.3.0"
        private const val FEIGN_OKHTTP_VERSION = "13.6"
        const val FEIGN = "org.springframework.cloud:spring-cloud-starter-openfeign:$FEIGN_VERSION"
        const val FEIGN_OKHTTP = "io.github.openfeign:feign-okhttp:$FEIGN_OKHTTP_VERSION"
    }

    object Kotlin {
        private const val VERSION = "1.9.25"
        private const val JACKSON_VERSION = "2.19.2"
        const val REFLECT = "org.jetbrains.kotlin:kotlin-reflect:$VERSION"
        const val JACKSON = "com.fasterxml.jackson.module:jackson-module-kotlin:$JACKSON_VERSION"
        const val TEST_JUNIT5 = "org.jetbrains.kotlin:kotlin-test-junit5:$VERSION"
    }

    object Database {
        const val MYSQL_CONNECTOR = "com.mysql:mysql-connector-j"
    }

    object JWT {
        private const val VERSION = "0.13.0"
        const val API = "io.jsonwebtoken:jjwt-api:$VERSION"
        const val IMPL = "io.jsonwebtoken:jjwt-impl:$VERSION"
        const val JACKSON = "io.jsonwebtoken:jjwt-jackson:$VERSION"
    }

    object Test {
        const val JUNIT_PLATFORM_LAUNCHER = "org.junit.platform:junit-platform-launcher"
    }

    object HTTPClient {
        private const val OKHTTP_VERSION = "4.12.0"
        const val OKHTTP = "com.squareup.okhttp3:okhttp:$OKHTTP_VERSION"
        const val OKHTTP_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:$OKHTTP_VERSION"
    }
}
