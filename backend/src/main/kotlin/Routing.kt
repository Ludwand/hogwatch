package com.example.hogwatch.backend

import com.example.hogwatch.shared.CreateSightingRequest
import com.example.hogwatch.shared.SightingSummary
import com.example.hogwatch.shared.Sightings
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.PriorityQueue
import kotlin.random.Random

const val MAX_SUBMISSION_PERIOD = 60 * 1000L
const val MAX_SUBMISSIONS_IN_PERIOD = 10

object LatestSubmission {
   private val counts = HashMap<String, Int>()
   private val times = PriorityQueue<QueueEntry>()

   private data class QueueEntry(val time: Long, val addr: String) : Comparable<QueueEntry> {
      override fun compareTo(other: QueueEntry): Int {
         return this.time.compareTo(other.time)
      }
   }

   fun canSubmit(addr: String): Boolean {
      val now = System.currentTimeMillis()
      while (times.isNotEmpty() && times.peek().time < (now - MAX_SUBMISSION_PERIOD)) {
         val entry = times.poll()
         val prev = counts[entry.addr] ?: continue
         counts[entry.addr] = prev - 1
      }

      val prev = counts[addr] ?: 0
      counts[addr] = prev + 1
      return (prev) < MAX_SUBMISSIONS_IN_PERIOD
   }
}

fun Application.configureRouting() {
   routing {
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

      get("/pins/{minLat}/{maxLat}/{minLong}/{maxLong}") {
         try {
            val minLat = call.parameters["minLat"]?.toDouble() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val maxLat = call.parameters["maxLat"]?.toDouble() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val minLong = call.parameters["minLong"]?.toDouble() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val maxLong = call.parameters["maxLong"]?.toDouble() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val sightings = Database.getSightingSummaries(minLat, maxLat, minLong, maxLong)
            call.respond(HttpStatusCode.OK, Sightings(sightings))
         } catch (e: Exception) {
            println("Exception ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
         }
      }

      get("/sightings/{userid}") {
         try {
            val userid = call.parameters["userid"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val sightings = Database.getSightingSummaries(userid)
            call.respond(HttpStatusCode.OK, Sightings(sightings))
         } catch (e: Exception) {
            println("Exception ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
         }
      }

      get("/sighting/{id}") {
         try {
            val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val sighting = Database.getSightingSummary(id) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, sighting)
         } catch (e: Exception) {
            println("Exception ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
         }
      }

      get("/sighting/{id}/image") {
         try {
            val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val image = Database.getSightingImage(id) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, image)
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
                       "${newSubmission.lon} at $readableTime. The hedgehos was ${newSubmission.isAlive}"
            ) //TODO: remove after testing is done

            val res = Database.insertSighting(
               newSubmission.userId,
               newSubmission.lat,
               newSubmission.lon,
               newSubmission.timeStamp,
               newSubmission.image,
               newSubmission.isAlive
            )
            if (res != null) {
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

      delete("/sightings/{id}/{userid}") {
         try {
            val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val userid = call.parameters["userid"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (Database.deleteSighting(id, userid)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.Unauthorized)
         } catch (e: Exception) {
            println("Exception ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
         }
      }
   }
}
