package ru.itpark.lessons.kafkatemplate.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

object Topics {
    const val STRING_TOPIC = "demo.strings"
    const val JSON_TOPIC = "demo.orders.json"
    const val PROTO_TOPIC = "demo.person.proto"
}

@Configuration
class KafkaTopics {
    @Bean
    fun stringTopic(): NewTopic =
        TopicBuilder
            .name(Topics.STRING_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun jsonTopic(): NewTopic =
        TopicBuilder
            .name(Topics.JSON_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun protoTopic(): NewTopic =
        TopicBuilder
            .name(Topics.PROTO_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
}
