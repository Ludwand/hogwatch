plugins {
   alias(libs.plugins.kotlin.jvm)
   alias(ktorLibs.plugins.ktor)
   alias(libs.plugins.kotlin.serialization)
}

application {
   mainClass = "com.example.hogwatch.backend.MainKt"
}

kotlin {
   jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
   implementation(ktorLibs.serialization.kotlinx.json)
   implementation(ktorLibs.server.callLogging)
   implementation(ktorLibs.server.contentNegotiation)
   implementation(ktorLibs.server.core)
   implementation(ktorLibs.server.netty)
   implementation(ktorLibs.server.statusPages)
   implementation(libs.logback.classic)
   implementation(libs.dataframe)
   implementation(libs.postgres)

   implementation(project(":shared"))

   testImplementation(kotlin("test"))
   testImplementation(ktorLibs.server.testHost)
   testImplementation(libs.ktor.client.content.negotiation)
}
