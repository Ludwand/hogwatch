package com.example.hogwatch.backend

import com.example.hogwatch.shared.LocalConfig
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer

fun main(args: Array<String>) {
    Database

    embeddedServer(
        factory = io.ktor.server.netty.Netty,
        port = LocalConfig.PORT,
        host = LocalConfig.BACKEND_URL,
        module = Application::rootModule
    ).start(wait = true)
}
