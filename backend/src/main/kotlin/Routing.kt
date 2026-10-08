package com.example.hogwatch.backend

import com.example.hogwatch.shared.CreateSightingRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.hogwatch.shared.SightingSummary
import com.example.hogwatch.shared.Sightings
import io.ktor.server.plugins.origin
import kotlin.random.Random

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
      // Invoking staticResources() enables your application to provide standard website content,
      // such as HTML and JavaScript files. Although this content can be executed within the browser,
      // it is considered static from the server's point of view.
      staticResources("/content", "mycontent")

      get("/get_map_info") {
         try {
            //TODO: retrieve data from the database
            val subArr: ArrayList<SightingSummary> = ArrayList()

            val amountMarkers = 1000
            for (i in 0 until amountMarkers)
            {
               subArr.add(SightingSummary(
                  id = 19826,
                  lat = Random.nextDouble(57.75, 58.25),
                  lon = Random.nextDouble(11.5, 12.5),
                  true
               ))
            }

            val mapInfo = Sightings(subArr)
            call.respond(mapInfo)

         } catch (e: Exception) {
            println("Exception ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
         }
      }

      post("/submission") {
         try {
            if (!LatestSubmission.canSubmit(call.request.origin.remoteAddress)) {
               println("Rejected submission from ${call.request.origin.remoteAddress}")
               call.respond(
                  HttpStatusCode.TooManyRequests,
                  "You are sending submissions too frequently."
               )
               return@post
            }

            val newSubmission = call.receive<CreateSightingRequest>()
            val instant = Instant.ofEpochSecond(newSubmission.timeStamp)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
               .withZone(ZoneId.systemDefault())
            val readableTime = formatter.format(instant)

            println(
               "Servern tog emot en observation från: " +
                       "${newSubmission.userId} på platsen lat: " +
                       "${newSubmission.lat} lon: " +
                       "${newSubmission.lon} at $readableTime"
            ) //TODO: remove after testing is done

            val res = Database.insertSighting(
               newSubmission.userId,
               newSubmission.lat,
               newSubmission.lon,
               newSubmission.timeStamp,
               newSubmission.image
            )
            if (res != null) { //TODO: dubble check if the error handling is correct
               call.respond(HttpStatusCode.Created, "Submission successfully received!")
            } else {
               call.respond(
                  HttpStatusCode.InternalServerError,
                  "Submission could not be saved on the database!"
               )
            }
         } catch (e: Exception) {
            println("Exception ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Problem occurred!")
         }
      }
   }
}
