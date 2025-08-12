package ru.itpark.lessons.kafkatemplate.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kafka")
data class KafkaAppProperties(
    var bootstrapServers: String = "localhost:9092",
    var schemaRegistryUrl: String? = null,
    var consumers: Consumers = Consumers(),
    var producers: Producers = Producers(),
) {
    data class Consumers(
        var common: Common = Common(),
        var strings: Consumer = Consumer(),
        var json: Consumer = Consumer(),
        var proto: Consumer = Consumer(),
    )

    data class Common(
        var autoOffsetReset: String = "earliest",
        var autoCommit: Boolean = true,
    )

    data class Consumer(
        var topic: String = "",
        var groupId: String = "",
    )

    data class Producers(
        var strings: Producer = Producer(),
        var json: Producer = Producer(),
        var proto: Producer = Producer(),
    )

    data class Producer(
        var topic: String = "",
    )
}
