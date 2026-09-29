package com.example.hogwatch.backend

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.hogwatch.shared.ManSub
import com.example.hogwatch.shared.MapInfo
import com.example.hogwatch.shared.Submission

fun Application.configureRouting() {
    routing {
        staticResources("/content", "mycontent") //Invoking staticResources() enables your application to provide standard website content, such as HTML and JavaScript files. Although this content can be executed within the browser, it is considered static from the server's point of view.

        get("/get_map_info") {
            try {
                //TODO: retrieve data from the database
                val sub1 = Submission(id = "19826", lat = "15.9846", lon = "-2.2941", timeStamp = System.currentTimeMillis())
                val subArr: ArrayList<Submission> = ArrayList()
                subArr.add(sub1)
                val mapInfo = MapInfo(subArr)

                call.respond(mapInfo)

            } catch (e: Exception) {
                println("Exception ${e.message}")
            }

        }

        post("/manual_submission"){
            try {
                val newSubmission = call.receive<ManSub>()
                val instant = Instant.ofEpochMilli(newSubmission.timeStamp)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault())
                val readableTime = formatter.format(instant)
                println("Servern tog emot en observation från: ${newSubmission.id} på platsen lat: ${newSubmission.lat} lon: ${newSubmission.lon} at ${readableTime}")
                Database.insertSighting(newSubmission.id.toInt(), newSubmission.lat.toFloat(), newSubmission.lon.toFloat(), newSubmission.timeStamp.toInt())
                call.respond(HttpStatusCode.Created, "Submission successfully received!")
            } catch (e: Exception) {
                println("Exception ${e.message}")
            }

        }
    }
}