package com.example.hogwatch.frontend

import com.example.hogwatch.shared.LocalConfig
import com.example.hogwatch.shared.ManSub
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    private var client: HttpClient? = null

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

    suspend fun manSub(id: String, lat: String, lon: String, timeStamp: Long): Boolean {
        val manualSubmissionSend = ManSub(id = id, lat = lat, lon = lon, timeStamp = timeStamp)
        return try {
            if (client?.isActive == true){
                val response = client!!.post{
                    url {
                        protocol = URLProtocol.HTTP
                        host = LocalConfig.FRONTEND_URL
                        port = LocalConfig.PORT
                        path("/manual_submission")
                    }
                    contentType(ContentType.Application.Json) // Tell the server that the following content will be JSON
                    setBody(manualSubmissionSend) // Add data class to body
                }
                if (response.status.isSuccess()) {
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
}

