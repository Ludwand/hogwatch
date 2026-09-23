package com.example

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.*
import io.ktor.client.statement.bodyAsText

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.*
import java.time.Instant

class ServerTest {

    @Test
    fun `test root endpoint`() = testApplication {
        application {
            rootModule()
        }
        // verify server root returns 200
        assertEquals(HttpStatusCode.OK, client.get("/").status)

    }
    @Test
    fun `test new endpoint`() = testApplication {
        application {
            rootModule() // Startar en helt ny, isolerad server för detta test
        }
        val response = client.get("/test1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("plain", response.contentType()?.contentSubtype)
        assertContains(response.bodyAsText(), "Hello From Ktor")
    }

    @Test
    fun `Manual Submission`() = testApplication {
        application{
            rootModule()
        }

        val client = createClient { //Creating JSON support
            install(ContentNegotiation) {
                json()
            }
        }

        val manualSubmissionSend = ManSub(id = "123", lat = "57.6282764", lon = "11.9030166", timeStamp = System.currentTimeMillis())

        val response = client.post("/manual_submission") {
            contentType(ContentType.Application.Json) // Tell the server that the following content will be JSON
            setBody(manualSubmissionSend) // Add data class to body
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("Submission successfully received!", response.bodyAsText())
    }
}

/*
* Lösning: Android-emulatorn har en specialadress för att nå den Mac/PC den körs på.
* I din app-kod måste du byta ut localhost mot 10.0.2.2.
* (Så adressen blir [http://10.0.2.2:8080/manual_submission](http://10.0.2.2:8080/manual_submission)).
*
*
* I frontend:Gå in i din apps AndroidManifest.xml (om det är den inbyggda Android-delen du jobbar i)
* och lägg till android:usesCleartextTraffic="true" under <application>-taggen:*/