plugins {
    kotlin(Plugins.KotlinJVM.ID) version Plugins.KotlinJVM.VERSION
    kotlin(Plugins.KotlinSpring.ID) version Plugins.KotlinSpring.VERSION
    id(Plugins.SpringBoot.ID) version Plugins.SpringBoot.VERSION
    id(Plugins.SpringDependencyManagement.ID) version Plugins.SpringDependencyManagement.VERSION
    id(Plugins.Ktlint.ID) version Plugins.Ktlint.VERSION
}

group = Project.GROUP
version = Project.VERSION
description = Project.DESCRIPTION

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Dependencies.SpringBoot.DATA_JDBC)
    implementation(Dependencies.SpringBoot.WEB)
    testImplementation(Dependencies.SpringBoot.TEST)

    implementation(Dependencies.SpringCloud.FEIGN)
    implementation(Dependencies.SpringCloud.FEIGN_OKHTTP)

    implementation(Dependencies.Kotlin.JACKSON)
    implementation(Dependencies.Kotlin.REFLECT)
    testImplementation(Dependencies.Kotlin.TEST_JUNIT5)

    runtimeOnly(Dependencies.Database.MYSQL_CONNECTOR)

    implementation(Dependencies.HTTPClient.OKHTTP)
    implementation(Dependencies.HTTPClient.OKHTTP_LOGGING_INTERCEPTOR)

    testRuntimeOnly(Dependencies.Test.JUNIT_PLATFORM_LAUNCHER)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
