package com.example.hogwatch.frontend
//
//
//
//fun `Manual Submission`() = testApplication {
//    application{
//        rootModule()
//    }
//
//    val client = createClient { //Creating JSON support
//        install(ContentNegotiation) {
//            json()
//        }
//    }
//
//    //val manualSubmissionSend = ManSub(id = "123", lat = "57.6282764", lon = "11.9030166", timeStamp = System.currentTimeMillis())
//
//    val response = client.post("/manual_submission") {
//        contentType(ContentType.Application.Json) // Tell the server that the following content will be JSON
//        //setBody(manualSubmissionSend) // Add data class to body
//    }
//    assertEquals(HttpStatusCode.Created, response.status)
//    assertEquals("Submission successfully received!", response.bodyAsText())
//}
//}