package com.example.hogwatch.frontend

import com.example.hogwatch.shared.LocalConfig
import com.example.hogwatch.shared.Submission
import com.example.hogwatch.shared.MapInfo
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
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

    suspend fun submit(id: String, lat: Double, lon: Double, timeStamp: Long, image: String?): Boolean {
        val manualSubmissionSend = Submission(id = id, lat = lat, lon = lon, timeStamp = timeStamp, image = image)
        return try {
            if (client?.isActive == true){ //client?.isActive can result in true, false or NULL. else handles false or NULL
                val response = client?.post{
                    url {
                        protocol = URLProtocol.HTTP
                        host = LocalConfig.FRONTEND_URL
                        port = LocalConfig.PORT
                        path("/submission")
                    }
                    contentType(ContentType.Application.Json) // Tell the server that the following content will be JSON
                    setBody(manualSubmissionSend) // Add data class to body
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

    suspend fun getMapInfo(): List<MapInfo> {
        return try {
            if (client?.isActive == true){
               val response = client?.get {
                    url {
                        protocol = URLProtocol.HTTP
                        host = LocalConfig.FRONTEND_URL
                        port = LocalConfig.PORT
                        path("/get_map_info")
                    }
                }
                if (response?.status?.isSuccess() == true){
                    val data: ArrayList<MapInfo> = response.body()
                    //println(data) //TODO: Only for testing
                    data
                } else {
                    println("Failed to fetch data")
                    emptyList()
                }

            } else {
                println("Client is not active")
                emptyList()
            }

        } catch (e: Exception){
            println("Exception: ${e.message}")
            emptyList()
        }

    }
}

