package org.example.kafka.serdes

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import org.example.kafka.UserInfoAggregate

class UserInfoAggregateJsonSerde : Serde<UserInfoAggregate> {

    override fun serializer() = UserInfoAggregateJsonSerializer()

    override fun deserializer() = UserInfoAggregateJsonDeserializer()
}


class UserInfoAggregateJsonDeserializer : Deserializer<UserInfoAggregate> {

    override fun deserialize(topic: String, data: ByteArray): UserInfoAggregate {
        return  Json.decodeFromString(data.toString(Charsets.UTF_8))
    }
}

class UserInfoAggregateJsonSerializer : Serializer<UserInfoAggregate> {

    override fun serialize(topic: String?, data: UserInfoAggregate?): ByteArray {
        return Json.encodeToString(data).toByteArray(Charsets.UTF_8)
    }
}