plugins {
    alias(libs.plugins.kotlin.jvm) version "2.4.20"
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "com.example.MainKt"
}

kotlin {
    jvmToolchain(17)
}
dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.statusPages)
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("org.jetbrains.kotlinx:dataframe-jdbc:1.0.0-rc01")
    implementation("org.postgresql:postgresql:42.7.12")

    implementation(project(":shared"))

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
    testImplementation("io.ktor:ktor-client-content-negotiation:${version}")
    //testImplementation(project(":shared"))
}
