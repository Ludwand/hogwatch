plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}
dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
}