plugins {
    kotlin(Plugins.KotlinJVM.ID) version Plugins.KotlinJVM.VERSION
    kotlin(Plugins.KotlinSpring.ID) version Plugins.KotlinSpring.VERSION
    id(Plugins.SpringBoot.ID) version Plugins.SpringBoot.VERSION
    id(Plugins.SpringDependencyManagement.ID) version Plugins.SpringDependencyManagement.VERSION
}

group = "app.xquare"
version = "0.0.1-SNAPSHOT"
description = "xquare-infra-v3"

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

    implementation(Dependencies.Kotlin.JACKSON)
    implementation(Dependencies.Kotlin.REFLECT)
    testImplementation(Dependencies.Kotlin.TEST_JUNIT5)

    runtimeOnly(Dependencies.Database.MYSQL_CONNECTOR)

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
