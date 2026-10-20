package com.example.hogwatch.backend

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureStatusPages() {
   install(StatusPages) {
      exception<IllegalStateException> { call, cause ->
         call.respondText("App in illegal state as ${cause.message}")
      }

      exception<Throwable> { call, cause ->
         call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
      }
   }
}
