package org.example.kafka.serdes

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import org.example.domain.RoleEvent

class RoleEventJsonSerde : Serde<RoleEvent> {

    override fun serializer() = RoleEventJsonSerializer()

    override fun deserializer() = RoleEventJsonDeserializer()
}

class RoleEventJsonDeserializer : Deserializer<RoleEvent> {

    override fun deserialize(topic: String, data: ByteArray): RoleEvent {
        return  Json.decodeFromString(data.toString(Charsets.UTF_8))
    }
}

class RoleEventJsonSerializer : Serializer<RoleEvent> {

    override fun serialize(topic: String?, data: RoleEvent?): ByteArray {
        return Json.encodeToString(data).toByteArray(Charsets.UTF_8)
    }
}
