package com.example.hogwatch.frontend.data.repository

import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.shared.Sighting
import com.example.hogwatch.shared.Sightings


object SightingRepository{

    private val sightings = Sightings

    suspend fun submit(id: String, lat: Double, lon: Double, timeStamp: Long, image: String?): Boolean {

        val manualSubmissionSend = Sighting(id = id, lat = lat, lon = lon, timeStamp = timeStamp, image = image)

        //TODO:validate input

        return NetworkClient.sendSightingToServer(manualSubmissionSend)
    }


}