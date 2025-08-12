package ru.itpark.lessons.kafkatemplate.consumer

import com.example.kafkademo.proto.Person
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.itpark.lessons.kafkatemplate.config.Topics
import ru.itpark.lessons.kafkatemplate.model.Order

@Component
class Listeners {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [Topics.STRING_TOPIC],
        groupId = "demo-string-group",
        containerFactory = "stringKafkaListenerContainerFactory",
    )
    fun onString(message: String) {
        log.info("STRING consumer got: {}", message)
    }

    @KafkaListener(
        topics = [Topics.JSON_TOPIC],
        groupId = "demo-json-group",
        containerFactory = "jsonKafkaListenerContainerFactory",
    )
    fun onJson(order: Order) {
        log.info("JSON consumer got: {}", order)
    }

    @KafkaListener(
        topics = [Topics.PROTO_TOPIC],
        groupId = "demo-proto-group",
        containerFactory = "protoKafkaListenerContainerFactory",
    )
    fun onProto(person: Person) {
        log.info(
            "PROTO consumer got: id={}, name={}, age={}, email={}",
            person.id,
            person.name,
            person.age,
            person.email,
        )
    }
}
