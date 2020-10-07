package org.example.kafka.serdes

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import org.example.domain.UserCode
import org.example.domain.UserInfo

class UserInfoJsonSerde : Serde<UserInfo> {

    override fun serializer() = UserInfoJsonSerializer()

    override fun deserializer() = UserInfoJsonDeserializer()
}


class UserInfoJsonDeserializer : Deserializer<UserInfo> {

    override fun deserialize(topic: String, data: ByteArray): UserInfo {
        return  Json.decodeFromString(data.toString(Charsets.UTF_8))
    }
}

class UserInfoJsonSerializer : Serializer<UserInfo> {

    override fun serialize(topic: String?, data: UserInfo?): ByteArray {
        return Json.encodeToString(data).toByteArray(Charsets.UTF_8)
    }
}