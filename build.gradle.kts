plugins {
    kotlin(Plugins.KotlinJVM.ID) version Plugins.KotlinJVM.VERSION
    kotlin(Plugins.KotlinSpring.ID) version Plugins.KotlinSpring.VERSION
    kotlin(Plugins.KotlinJpa.ID) version Plugins.KotlinJpa.VERSION
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
    implementation(Dependencies.SpringBoot.DATA_JPA)
    implementation(Dependencies.SpringBoot.WEB)
    implementation(Dependencies.SpringBoot.SECURITY)
    implementation(Dependencies.SpringBoot.VALIDATION)
    implementation(Dependencies.SpringBoot.WEBFLUX)
    testImplementation(Dependencies.SpringBoot.TEST)
    implementation(Dependencies.SpringBoot.DATA_REDIS)
    implementation(Dependencies.SpringBoot.MAIL)
    implementation(Dependencies.SpringBoot.THYMELEAF)

    implementation(Dependencies.SpringCloud.FEIGN)
    implementation(Dependencies.SpringCloud.FEIGN_OKHTTP)

    implementation(Dependencies.SpringDoc.OPENAPI)

    implementation(Dependencies.Kotlin.JACKSON)
    implementation(Dependencies.Kotlin.JACKSON_DATAFORMAT_YAML)
    implementation(Dependencies.Kotlin.REFLECT)
    implementation(Dependencies.Kotlin.COROUTINES)
    implementation(Dependencies.Kotlin.COROUTINES_REACTOR)
    testImplementation(Dependencies.Kotlin.TEST_JUNIT5)
    testRuntimeOnly(Dependencies.Database.H2)

    runtimeOnly(Dependencies.Database.MYSQL_CONNECTOR)

    implementation(Dependencies.HTTPClient.OKHTTP)
    implementation(Dependencies.HTTPClient.OKHTTP_LOGGING_INTERCEPTOR)

    implementation(Dependencies.JWT.API)
    runtimeOnly(Dependencies.JWT.IMPL)
    runtimeOnly(Dependencies.JWT.JACKSON)

    implementation(Dependencies.SpringBoot.MAIL)

    implementation(Dependencies.SpringBoot.DATA_REDIS)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
