package com.example

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Application.configureRouting() {
    routing {
        staticResources("/content", "mycontent") //Invoking staticResources() enables your application to provide standard website content, such as HTML and JavaScript files. Although this content can be executed within the browser, it is considered static from the server's point of view.
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
        get("/test1") {
            val text = "<h1>Hello From Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
    }
}