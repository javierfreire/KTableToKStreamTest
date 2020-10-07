package org.example.cli

import kotlinx.cli.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serdes
import org.example.kafka.KTableToKStreamTestApp
import org.example.domain.RoleEvent
import org.example.domain.UserCode
import org.example.kafka.serdes.RoleEventJsonDeserializer
import org.example.kafka.serdes.RoleEventJsonSerde
import org.example.kafka.serdes.UserCodeJsonDeserializer
import org.example.kafka.serdes.UserCodeJsonSerde
import java.time.Duration
import java.util.*

@ExperimentalCli
class SpyOutput : Subcommand("spy", "Spy Output") {

    private val topic by option(ArgType.String, "topic", "t", "OutputTopic")
        .default(KTableToKStreamTestApp.defaultOutputTopic)
    private val bootstrapServers by option(ArgType.String, "servers", "s", "Bootstrap Servers")
        .default(KTableToKStreamTestApp.defaultBootstrapServers)

    override fun execute() {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        properties[ConsumerConfig.CLIENT_ID_CONFIG] = UUID.randomUUID().toString()
        properties[ConsumerConfig.GROUP_ID_CONFIG] = UUID.randomUUID().toString()
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = UserCodeJsonDeserializer::class.qualifiedName
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = RoleEventJsonDeserializer::class.qualifiedName
        val consumer = KafkaConsumer<UserCode, RoleEvent>(properties)
        consumer.subscribe(listOf(topic))

        while (true) {
            val records = consumer.poll(Duration.ofSeconds(1))

            if (records.count() > 0) {
                println("----------------")

                records.forEach {
                    println(">>>>>> ${it.key()} => ${it.value()}")
                }
            }
        }
    }
}