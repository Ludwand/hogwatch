package com.example.hogwatch.shared
import kotlinx.serialization.Serializable

@Serializable
data class Sightings(
    var submissions: ArrayList<Sighting> = ArrayList<Sighting>()
)


@Serializable
data class Sighting(
    val id: String?,
    val lat: Double,//The precision is needed for latitude and longitude
    val lon: Double,
    val timeStamp: Long,
    val image: String?,
) {

}