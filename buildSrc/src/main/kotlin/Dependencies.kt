object Dependencies {
    object SpringBoot {
        const val DATA_JPA = "org.springframework.boot:spring-boot-starter-data-jpa"
        const val WEB = "org.springframework.boot:spring-boot-starter-web"
        const val SECURITY = "org.springframework.boot:spring-boot-starter-security"
        const val VALIDATION = "org.springframework.boot:spring-boot-starter-validation"
        const val WEBFLUX = "org.springframework.boot:spring-boot-starter-webflux"
        const val TEST = "org.springframework.boot:spring-boot-starter-test"
    }

    object Kotlin {
        const val REFLECT = "org.jetbrains.kotlin:kotlin-reflect"
        const val JACKSON = "com.fasterxml.jackson.module:jackson-module-kotlin"
        const val TEST_JUNIT5 = "org.jetbrains.kotlin:kotlin-test-junit5"
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
}
