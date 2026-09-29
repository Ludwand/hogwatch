package com.example.hogwatch.shared
import kotlinx.serialization.Serializable

@Serializable
data class MapInfo(
    var submissions: ArrayList<Submission> = ArrayList<Submission>()
)


@Serializable
data class Submission(
    val id: String,
    val lat: Double,//The precision is needed for latitude and longitude
    val lon: Double,
    val timeStamp: Long,
    val image: String?,
) {

}