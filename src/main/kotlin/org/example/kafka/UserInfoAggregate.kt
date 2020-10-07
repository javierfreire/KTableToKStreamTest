package org.example.kafka

import kotlinx.serialization.Serializable
import org.example.domain.RoleEvent
import org.example.domain.UserInfo
import org.example.domain.diffRoles

@Serializable
data class UserInfoAggregate(
    val currentUserInfo: UserInfo? = null,
    val rolesToEmit: Collection<RoleEvent> = emptyList()
) {

    fun next(userInfo: UserInfo)= UserInfoAggregate(
        currentUserInfo = userInfo,
        rolesToEmit = currentUserInfo.diffRoles(userInfo)
    )
}