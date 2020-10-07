package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val roles: Set<String>
) {

    fun diffRoles(userInfo: UserInfo) : Collection<RoleEvent> {
        return (roles - userInfo.roles).map { RoleRemoved(it) } +
            (userInfo.roles - roles).map { RoleAdded(it) }
    }
}

fun UserInfo?.diffRoles(userInfo: UserInfo) : Collection<RoleEvent> {
    return this?.diffRoles(userInfo) ?: userInfo.roles.map { RoleAdded(it) }
}