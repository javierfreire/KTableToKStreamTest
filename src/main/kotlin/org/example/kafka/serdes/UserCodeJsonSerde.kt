package org.example.kafka.serdes

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import org.example.domain.UserCode

class UserCodeJsonSerde : Serde<UserCode> {

    override fun serializer() = UserCodeJsonSerializer()

    override fun deserializer() = UserCodeJsonDeserializer()
}

class UserCodeJsonDeserializer : Deserializer<UserCode> {

    override fun deserialize(topic: String, data: ByteArray): UserCode {
        return  Json.decodeFromString(data.toString(Charsets.UTF_8))
    }
}

class UserCodeJsonSerializer : Serializer<UserCode> {

    override fun serialize(topic: String?, data: UserCode?): ByteArray {
        return Json.encodeToString(data).toByteArray(Charsets.UTF_8)
    }
}