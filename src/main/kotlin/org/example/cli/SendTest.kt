package org.example.cli

import kotlinx.cli.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serdes
import org.example.domain.UserCode
import org.example.kafka.KTableToKStreamTestApp
import org.example.domain.UserInfo
import org.example.kafka.serdes.*
import java.util.*

@ExperimentalCli
class SendTest : Subcommand("test", "Send test to input topic") {

    private val topic by option(ArgType.String, "topic", "t", "Input topic")
        .default(KTableToKStreamTestApp.defaultInputTopic)
    private val bootstrapServers by option(ArgType.String, "servers", "s", "Bootstrap Servers")
        .default(KTableToKStreamTestApp.defaultBootstrapServers)
    private val waitTimeToStart by option(ArgType.Int, "waitTimeToStart", "W", "Wait time to start")
        .default(2000)
    private val waitTimeBetweenMessages by option(ArgType.Int, "waitTimeBetweenMessages", "w", "Wait time between messages")
        .default(2000)

    override fun execute() {
        Thread.sleep(waitTimeToStart.toLong())

        val producer = createProducer()
        send(producer, UserCode("PeterParker"), UserInfo(roles = setOf("student", "superhero")))
        send(producer, UserCode("PeterParker"), UserInfo(roles = setOf("student", "superhero")))

        if (waitTimeBetweenMessages != 0) {
            Thread.sleep(waitTimeBetweenMessages.toLong())
        }

        send(producer, UserCode("PeterParker"), UserInfo(roles = setOf("photographer", "superhero")))
        send(producer, UserCode("PeterParker"), UserInfo(roles = setOf("photographer", "superhero")))
        producer.close()
    }

    private fun createProducer(): Producer<UserCode, UserInfo> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.RETRIES_CONFIG] = 0
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 16384
        props[ProducerConfig.LINGER_MS_CONFIG] = 0
        props[ProducerConfig.BUFFER_MEMORY_CONFIG] = 33554432
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = UserCodeJsonSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = UserInfoJsonSerializer::class.java.name
        return KafkaProducer(props)
    }

    private fun send(
        producer: Producer<UserCode, UserInfo>,
        key: UserCode,
        message: UserInfo
    ) {
        val record = ProducerRecord(topic, key, message)
        producer.send(record)
        println("Sended: $key $message")
    }
}