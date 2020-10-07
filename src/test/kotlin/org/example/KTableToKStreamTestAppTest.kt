package org.example

import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.example.domain.*
import org.example.kafka.KTableToKStreamTestApp
import org.example.kafka.serdes.RoleEventJsonSerde
import org.example.kafka.serdes.UserCodeJsonSerde
import org.example.kafka.serdes.UserInfoJsonSerde
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KTableToKStreamTestAppTest {

    private lateinit var inputTopic: TestInputTopic<UserCode, UserInfo>
    private lateinit var outputTopic: TestOutputTopic<UserCode, RoleEvent>
    private lateinit var testDriver: TopologyTestDriver

    @BeforeEach
    fun setup() {
        val app = KTableToKStreamTestApp()
        testDriver = TopologyTestDriver(app.topology(), app.kafkaProperties()).apply {
            inputTopic = createInputTopic(
                app.inputTopic,
                UserCodeJsonSerde().serializer(),
                UserInfoJsonSerde().serializer()
            )

            outputTopic = createOutputTopic(
                app.outputTopic,
                UserCodeJsonSerde().deserializer(),
                RoleEventJsonSerde().deserializer()
            )
        }
    }

    @AfterEach
    fun clear() {
        testDriver.close()
    }

    @Test
    fun duplicate_events() {
        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("student", "superhero")))
        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("student", "superhero")))
        assertEquals(RoleAdded("student"), outputTopic.readValue())
        assertEquals(RoleAdded("superhero"), outputTopic.readValue())
        assertTrue(outputTopic.isEmpty)

        // a while ...

        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("photographer", "superhero")))
        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("photographer", "superhero")))
        assertEquals(RoleRemoved("student"), outputTopic.readValue())
        assertEquals(RoleAdded("photographer"), outputTopic.readValue())
        assertTrue(outputTopic.isEmpty)
    }
}