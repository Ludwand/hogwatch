package com.example.hogwatch.frontend.data.repository

import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.shared.Submission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

//class SightingRepository(){
//    suspend fun submit(id: String, lat: Double, lon: Double, timeStamp: Long, image: String?): Boolean {
//        val manualSubmissionSend = Submission(id = id, lat = lat, lon = lon, timeStamp = timeStamp, image = image)
//
//        //TODO:validate input
//
//        return NetworkClient.sendSightingToServer(manualSubmissionSend)
//    }
//
//
//}