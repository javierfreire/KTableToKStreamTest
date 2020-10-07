package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class RoleEvent {

    abstract val role: String

}

@Serializable
data class RoleAdded(override val role: String) : RoleEvent()

@Serializable
data class RoleRemoved(override val role: String) : RoleEvent()