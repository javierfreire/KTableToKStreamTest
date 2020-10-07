# KTableToKStreamTest

This project is a test to show how events can be lost in an aggregate used to compare the previous and current value of a record.

This is intended to serve as an explanation for kafka improvement proposal
[KIP-675](https://cwiki.apache.org/confluence/display/KAFKA/KIP-675%3A+Convert+KTable+to+a+KStream+using+the+previous+value)

The input stream receives the user with their roles and is expected to obtain in the output stream if a role has been added or removed.

In this test duplicate entries are received, something that never happens ;-)

```
        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("student", "superhero")))
        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("student", "superhero")))
        assertEquals(RoleAdded("student"), outputTopic.readValue())
        assertEquals(RoleAdded("superhero"), outputTopic.readValue())
        assertTrue(outputTopic.isEmpty)

        // a while ....

        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("photographer", "superhero")))
        inputTopic.pipeInput(UserCode("PeterParker"), UserInfo(roles = setOf("photographer", "superhero")))
        assertEquals(RoleRemoved("student"), outputTopic.readValue())
        assertEquals(RoleAdded("photographer"), outputTopic.readValue())
        assertTrue(outputTopic.isEmpty)
```

To implement the solution, I designed the next aggregate and topology

```
        data class UserInfoAggregate(
            val currentUserInfo: UserInfo? = null,
            val rolesToEmit: Collection<RoleEvent> = emptyList()
        ) {
        
            fun next(userInfo: UserInfo)= UserInfoAggregate(
                currentUserInfo = userInfo,
                rolesToEmit = currentUserInfo.diffRoles(userInfo)
            )
        }
```

```
        inputStream
            .groupByKey()
            .aggregate(::UserInfoAggregate) { _, userInfo, aggregate -> aggregate.next(userInfo) }
            .toStream()
            .flatMapValues { _, value -> value.rolesToEmit }
            .to(outputTopic)
```

This code works if the commit interval is zero. If the commit interval is greater than zero and the same record is 
received twice, it does not emit any result. The aggregate of the second event removes the events to be emitted.

You can run the example with

1. Build the project:
```
make 
```

2. Start kafka
```
make local-env
```

3. Launch test with commit interval to 0 ms
```
make test-0-commit
```

4. Launch test with commit interval to 1000 ms
```
make test-1000-commit
```