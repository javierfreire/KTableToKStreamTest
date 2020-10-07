package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserCode(
    val value: String
)