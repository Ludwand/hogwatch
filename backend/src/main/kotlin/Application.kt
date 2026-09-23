package com.example.hogwatch.backend

import io.ktor.server.application.Application

fun Application.rootModule() {
    configureStatusPages()
    configureSerialization()
    configureRouting()
}
