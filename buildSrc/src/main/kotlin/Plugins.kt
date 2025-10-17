object Plugins {
    private const val KOTLIN_VERSION = "1.9.25"

    object KotlinJVM {
        const val ID = "jvm"
        const val VERSION = KOTLIN_VERSION
    }

    object KotlinSpring {
        const val ID = "plugin.spring"
        const val VERSION = KOTLIN_VERSION
    }

    object KotlinJpa {
        const val ID = "plugin.jpa"
        const val VERSION = KOTLIN_VERSION
    }

    object SpringBoot {
        const val ID = "org.springframework.boot"
        const val VERSION = "3.5.5"
    }

    object SpringDependencyManagement {
        const val ID = "io.spring.dependency-management"
        const val VERSION = "1.1.7"
    }

    object Ktlint {
        const val ID = "org.jlleitschuh.gradle.ktlint"
        const val VERSION = "13.1.0"
    }
}
