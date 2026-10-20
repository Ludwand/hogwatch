package com.example.hogwatch.frontend.data.remote

import com.example.hogwatch.shared.CreateSightingRequest
import com.example.hogwatch.shared.LocalConfig
import com.example.hogwatch.shared.SightingExtraData
import com.example.hogwatch.shared.Sightings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

object NetworkClient {
   private var client: HttpClient? = null //? enable client to be NULL

   fun initClient() {
      if (client == null) {
         client = HttpClient(CIO) {
            install(ContentNegotiation) {
               json(Json {
                  prettyPrint = true //Increase readability
                  isLenient = true
                  ignoreUnknownKeys = true //Avoid serializationException
               })
            }
         }
      }
   }

   fun closeClient() {
      client?.close()
      client = null
   }

   suspend fun sendSightingToServer(submission: CreateSightingRequest): Boolean {
      return try {
         if (client?.isActive == true) { //client?.isActive can result in true, false or NULL. else handles false or NULL
            val response = client?.post {
               url {
                  protocol = URLProtocol.HTTP
                  host = LocalConfig.FRONTEND_URL
                  port = LocalConfig.PORT
                  path("/submission")
               }
               contentType(ContentType.Application.Json) // Tell the server that the following content will be JSON
               setBody(submission) // Add data class to body
            }
            if (response?.status?.isSuccess() == true) {
               println("Successfully submitted manual submission")
               true
            } else {
               println("Failed to submit manual submission")
               false
            }
         } else {
            println("Client is not active")
            false
         }
      } catch (e: Exception) {
         println("Exception: ${e.message}")
         false
      }
   }

   suspend fun getMapPins(
      minLat: Double,
      maxLat: Double,
      minLon: Double,
      maxLon: Double
   ): Sightings {
      val http = client?.takeIf { it.isActive } ?: return Sightings()

      return try {
         val response = http.get {
            url {
               protocol = URLProtocol.HTTP
               host = LocalConfig.FRONTEND_URL
               port = LocalConfig.PORT
               path("pins", "$minLat", "$maxLat", "$minLon", "$maxLon")
            }
         }

         if (response.status.isSuccess())
            response.body()
         else
            Sightings()

      } catch (e: Exception) {
         println("Failed to fetch pins: ${e.message}")
         Sightings()
      }
   }

   suspend fun getMapPinInfo(id: Int): SightingExtraData? {
      val http = client?.takeIf { it.isActive } ?: return null

      return try {
         val response = http.get {
            url {
               protocol = URLProtocol.HTTP
               host = LocalConfig.FRONTEND_URL
               port = LocalConfig.PORT
               path("sighting", "$id", "image")
            }
         }

         if (response.status.isSuccess())
            SightingExtraData(id, response.body<String>())
         else
            null

      } catch (e: Exception) {
         println("Failed to fetch image: ${e.message}")
         null
      }
   }

   suspend fun getMapInfo(): Sightings {
      return try {
         if (client?.isActive == true) {
            val response = client?.get {
               url {
                  protocol = URLProtocol.HTTP
                  host = LocalConfig.FRONTEND_URL
                  port = LocalConfig.PORT
                  path("/get_map_info")
               }
            }
            if (response?.status?.isSuccess() == true) {
               val data: Sightings = response.body()
               //println(data) //TODO: Only for testing
               data
            } else {
               println("Failed to fetch data")
               Sightings()
            }

         } else {
            println("Client is not active")
            Sightings()
         }

      } catch (e: Exception) {
         println("Exception: ${e.message}")
         Sightings()
      }

   }

   suspend fun getUserSightings(userId: String): Sightings {
      val http = client?.takeIf { it.isActive } ?: return Sightings()

      return try {
         val response = http.get {
            url {
               protocol = URLProtocol.HTTP
               host = LocalConfig.FRONTEND_URL
               port = LocalConfig.PORT
               path("sightings", userId)
            }
         }

         if (response.status.isSuccess()) {
            response.body()
         } else {
            Sightings()
         }
      } catch (e: Exception) {
         println("Failed to fetch user sightings: ${e.message}")
         Sightings()
      }
   }

   suspend fun deleteSighting(id: Int, userId: String): Boolean {
      val http = client?.takeIf { it.isActive } ?: return false

      return try {
         val response = http.delete {
            url {
               protocol = URLProtocol.HTTP
               host = LocalConfig.FRONTEND_URL
               port = LocalConfig.PORT
               path("sightings", "$id", userId)
            }
         }
         response.status.isSuccess()
      } catch (e: Exception) {
         println("Failed to delete sighting: ${e.message}")
         false
      }
   }
}

