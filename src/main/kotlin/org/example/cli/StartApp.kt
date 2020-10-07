package org.example.cli

import kotlinx.cli.*
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.KafkaStreams
import org.example.kafka.KTableToKStreamTestApp
import java.util.*
import java.util.concurrent.CountDownLatch

@ExperimentalCli
class StartApp : Subcommand("start", "Start app") {

    private val inputTopic by option(ArgType.String, "input", "i", "Input Topic")
        .default(KTableToKStreamTestApp.defaultInputTopic)
    private val outputTopic by option(ArgType.String, "output", "o", "Output Topic")
        .default(KTableToKStreamTestApp.defaultOutputTopic)
    private val bootstrapServers by option(ArgType.String, "servers", "s", "Bootstrap Servers")
        .default(KTableToKStreamTestApp.defaultBootstrapServers)
    private val commitIntervalMs by option(ArgType.Int, "commit", "c", "Commit interval ms")
        .default(KTableToKStreamTestApp.defaultCommitIntervalMs)

    override fun execute() {
        cleanTopics();

        val app = KTableToKStreamTestApp(
            inputTopic = inputTopic,
            outputTopic = outputTopic,
            commitIntervalMs = commitIntervalMs.toLong(),
            bootstrapServers = bootstrapServers
        )

        val streams = KafkaStreams(app.topology(), app.kafkaProperties())

        val latch = CountDownLatch(1)

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(object : Thread("streams-shutdown-hook") {

            override fun run() {
                streams.close()
                latch.countDown()
            }
        })

        try {
            streams.cleanUp()
            streams.start()
            latch.await()
        } catch (e: Throwable) {
            e.printStackTrace();
            kotlin.system.exitProcess(1)
        }

        kotlin.system.exitProcess(0)
    }

    private fun cleanTopics() {
        val properties = Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers

        val admin: AdminClient = AdminClient.create(properties)
        val topicsToDelete = mutableListOf<String>()

        admin.listTopics().listings().get().forEach {
            val topic = it.name()

            if (topic.startsWith(KTableToKStreamTestApp.defaultApplicationId)) {
                topicsToDelete.add(topic)
            }
        }

        admin.deleteTopics(topicsToDelete).all().get()

        admin.createTopics(listOf(
            NewTopic(inputTopic, 1, 1),
            NewTopic(outputTopic, 1, 1)
        )).all().get()
    }
}