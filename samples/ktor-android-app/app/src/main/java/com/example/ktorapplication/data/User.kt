package com.example.ktorapplication.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("address")
    val address: String,
    @SerialName("lastName")
    val lastName: String,
)