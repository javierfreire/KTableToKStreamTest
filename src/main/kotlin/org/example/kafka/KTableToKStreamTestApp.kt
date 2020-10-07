package org.example.kafka

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.example.kafka.serdes.RoleEventJsonSerde
import org.example.kafka.serdes.UserCodeJsonSerde
import org.example.kafka.serdes.UserInfoAggregateJsonSerde
import org.example.kafka.serdes.UserInfoJsonSerde
import java.util.*

class KTableToKStreamTestApp(
    val inputTopic: String = defaultInputTopic,
    val outputTopic: String = defaultOutputTopic,
    val commitIntervalMs: Long = defaultCommitIntervalMs.toLong(),
    private val bootstrapServers: String = defaultBootstrapServers,
    private val applicationId: String = "kTableToKStreamTestApp"
) {

    fun topology() : Topology {
        val streamsBuilder = StreamsBuilder()

        streamsBuilder
            .stream(inputTopic, Consumed.with(UserCodeJsonSerde(), UserInfoJsonSerde()))
            .peek { key, value -> println("Received $key : $value")}
            .groupByKey()
            .aggregate(::UserInfoAggregate,
                { _, userInfo, aggregate -> aggregate.next(userInfo) },
                Materialized.with(UserCodeJsonSerde(), UserInfoAggregateJsonSerde())
            )
            .toStream()
            .peek { key, value -> println("Aggregate $key : $value")}
            .flatMapValues { _, value -> value.rolesToEmit }
            .peek { key, value -> println("Events $key : $value")}
            .to(outputTopic, Produced.with(UserCodeJsonSerde(), RoleEventJsonSerde()))

        return streamsBuilder.build()
    }

    fun kafkaProperties() = Properties().apply {
        this[StreamsConfig.APPLICATION_ID_CONFIG] = applicationId
        this[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        this[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = commitIntervalMs
    }

    companion object {

        const val defaultApplicationId = "kTableToKStreamTestApp"

        const val defaultInputTopic = "$defaultApplicationId-input"

        const val defaultOutputTopic = "$defaultApplicationId-output"

        const val defaultBootstrapServers = "localhost:9092"

        const val defaultCommitIntervalMs = 1000
    }
}
