plugins {
    kotlin("jvm") version "2.3.21"
    application
}

group = "ru.hukm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("ru.hukm.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
