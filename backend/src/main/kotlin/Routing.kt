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
import com.example.hogwatch.shared.MapInfo
import com.example.hogwatch.shared.Submission
import io.ktor.server.plugins.origin

const val MIN_SUBMISSION_DELAY = 60 * 1000L

object LatestSubmission {
    private var data = HashMap<String, Long>()

    fun canSubmit(addr: String): Boolean {
        val now = System.currentTimeMillis()
        val old = data.put(addr, now)
        return old == null || now - old > MIN_SUBMISSION_DELAY
    }
}

fun Application.configureRouting() {
    routing {
        staticResources("/content", "mycontent") //Invoking staticResources() enables your application to provide standard website content, such as HTML and JavaScript files. Although this content can be executed within the browser, it is considered static from the server's point of view.

        get("/get_map_info") {
            try {
                //TODO: retrieve data from the database
                val subArr: ArrayList<Submission> = ArrayList()

                val sub1 = Submission(
                    id = "19826",
                    lat = 15.9846,
                    lon = -2.2941,
                    timeStamp = System.currentTimeMillis(),
                    null)
                subArr.add(sub1)

                val sub2 = Submission(
                    id = "1",
                    lat = 57.71,
                    lon = 11.97,
                    timeStamp = System.currentTimeMillis(),
                    null)
                subArr.add(sub2)

                val mapInfo = MapInfo(subArr)
                call.respond(mapInfo)

            } catch (e: Exception) {
                println("Exception ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            }
        }

        post("/submission"){
            try {
                if (!LatestSubmission.canSubmit(call.request.origin.remoteAddress)) {
                    println("Rejected submission from ${call.request.origin.remoteAddress}")
                    call.respond(HttpStatusCode.TooManyRequests, "You are sending submissions too frequently.")
                    return@post
                }

                val newSubmission = call.receive<Submission>()
                val instant = Instant.ofEpochSecond(newSubmission.timeStamp)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault())
                val readableTime = formatter.format(instant)
                println("Servern tog emot en observation från: ${newSubmission.id} på platsen lat: ${newSubmission.lat} lon: ${newSubmission.lon} at $readableTime") //TODO: remove after testing is done
                val res = Database.insertSighting(newSubmission.id, newSubmission.lat, newSubmission.lon, newSubmission.timeStamp, newSubmission.image)
                if (res != null){ //TODO: dubble check if the error handling is correct
                    call.respond(HttpStatusCode.Created, "Submission successfully received!")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Submission could not be saved on the database!")
                }
            } catch (e: Exception) {
                println("Exception ${e.message}")
                call.respond(HttpStatusCode.BadRequest, "Problem occurred!")
            }
        }
    }
}
