
import org.jetbrains.dokka.gradle.DokkaTask

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val sqlite_jdbc_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("org.jetbrains.dokka") version "1.9.10"
}

group = "org.gycoding"
version = "1.0.0"

application {
    mainClass.set("org.gycoding.ApplicationKt")
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/dokka")
}

ktor {
    fatJar {
        archiveFileName.set("GYServer.jar")
    }
    docker {
        jreVersion.set(JavaVersion.VERSION_11)
        localImageName.set("GYServer")
        imageTag.set("v1.0.0")
    }
}

tasks {
    val dokkaHtml by getting(DokkaTask::class) {
        outputDirectory.set(buildDir.resolve("../docs"))
    }
}

dependencies {
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.xerial:sqlite-jdbc:$sqlite_jdbc_version")
    implementation("com.mysql:mysql-connector-j:8.0.33")
}
