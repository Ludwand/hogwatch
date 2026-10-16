package com.example.hogwatch.shared

import kotlinx.serialization.Serializable

@Serializable
data class Sightings(
   var submissions: ArrayList<SightingSummary> = ArrayList<SightingSummary>()
)


@Serializable
data class CreateSightingRequest(
   val userId: String,
   val lat: Double,
   val lon: Double,
   val timeStamp: Long,
   val image: String?
)

@Serializable
data class SightingSummary(
   val id: Int,
   val lat: Double,
   val lon: Double,
   val hasImage: Boolean
)

@Serializable
data class SightingExtraData(
   val id: Int,
   val image: String?
)