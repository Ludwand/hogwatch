package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
data class ManSub(
    val id: String,
    val lat: String,
    val lon: String,
    val timeStamp: Long
)