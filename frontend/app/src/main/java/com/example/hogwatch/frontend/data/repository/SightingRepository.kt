package com.example.hogwatch.frontend.data.repository

import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.shared.CreateSightingRequest
import com.example.hogwatch.shared.Sightings
import java.sql.Types.NULL

object SightingRepository {
   private val sightings = Sightings
   suspend fun submit(
      userId: String,
      lat: Double,
      lon: Double,
      timeStamp: Long,
      image: String?,
      isAlive: String
   ): Boolean {
      val manualSubmissionSend = CreateSightingRequest(
         userId = userId,
         lat = lat,
         lon = lon,
         timeStamp = timeStamp,
         image = image,
         isAlive = isAlive
      )
      return NetworkClient.sendSightingToServer(manualSubmissionSend)
   }
}