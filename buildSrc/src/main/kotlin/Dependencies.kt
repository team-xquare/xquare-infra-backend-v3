object Dependencies {
    object SpringBoot {
        const val DATA_JDBC = "org.springframework.boot:spring-boot-starter-data-jdbc"
        const val WEB = "org.springframework.boot:spring-boot-starter-web"
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

    object Test {
        const val JUNIT_PLATFORM_LAUNCHER = "org.junit.platform:junit-platform-launcher"
    }
}
