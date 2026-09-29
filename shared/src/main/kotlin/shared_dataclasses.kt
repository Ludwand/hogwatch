package com.example.hogwatch.shared
import kotlinx.serialization.Serializable

@Serializable
data class ManSub( //Right not this is a copy of Submissions but this might be useful if one of the classes need to change but not the other
    val id: String,
    val lat: String,
    val lon: String,
    val timeStamp: Long
)


@Serializable
data class MapInfo(
    var submissions: ArrayList<Submission> = ArrayList<Submission>()
)


@Serializable
data class Submission(
    val id: String,
    val lat: String,
    val lon: String,
    val timeStamp: Long,
)