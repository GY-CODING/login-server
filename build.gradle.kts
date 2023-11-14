import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val sqlite_jdbc_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
}

group = "org.gycoding"
version = "1.0.0"

application {
    mainClass.set("org.gycoding.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks.jar {
    from("/accounts.db")
}


ktor {
    fatJar {
        archiveFileName.set("GYServer.jar")
    }
    docker {
        jreVersion.set(JavaVersion.VERSION_11)
        localImageName.set("GYServer")
        imageTag.set("v1.0.0")
        /*externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "ktor-app" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )*/
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
}
