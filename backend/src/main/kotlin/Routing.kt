package com.example.hogwatch.backend


import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.shared.ManSub

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

        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }

        post("/manual_submission"){
            val newSubmission = call.receive<ManSub>()
            val instant = Instant.ofEpochMilli(newSubmission.timeStamp)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault())
            val readableTime = formatter.format(instant)
            println("Servern tog emot en observation från: ${newSubmission.id} på platsen lat: ${newSubmission.lat} lon: ${newSubmission.lon} at ${readableTime}")
            Database.insertSighting(newSubmission.id.toInt(), newSubmission.lat.toFloat(), newSubmission.lon.toFloat(), newSubmission.timeStamp.toInt())
            call.respond(HttpStatusCode.Created, "Submission successfully received!")
        }
    }
}