package ru.itpark.lessons.kafkatemplate.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.itpark.lessons.kafkatemplate.model.Order

@Configuration
class KafkaJsonConfig(
    private val app: KafkaAppProperties,
) {
    // ---------- PRODUCER (JSON) ----------
    @Bean
    fun jsonProducerFactory(): ProducerFactory<String, Order> {
        val p =
            mutableMapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to app.bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            )
        app.schemaRegistryUrl?.let { p["schema.registry.url"] = it }
        return DefaultKafkaProducerFactory(p)
    }

    @Bean
    fun jsonKafkaTemplate(): KafkaTemplate<String, Order> =
        KafkaTemplate(jsonProducerFactory()).apply {
            defaultTopic = app.producers.json.topic
        }

    @Bean
    fun jsonConsumerFactory(): ConsumerFactory<String, Order> {
        val c =
            mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to app.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to
                    app.consumers.json.groupId
                        .ifBlank { "demo-json-group" },
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to app.consumers.common.autoOffsetReset,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to app.consumers.common.autoCommit,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "ru.itpark.lessons.kafkatemplate.*",
            )
        app.schemaRegistryUrl?.let { c["schema.registry.url"] = it }
        return DefaultKafkaConsumerFactory(c)
    }

    @Bean(name = ["jsonKafkaListenerContainerFactory"])
    fun jsonKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Order> =
        ConcurrentKafkaListenerContainerFactory<String, Order>().apply {
            consumerFactory = jsonConsumerFactory()
            setConcurrency(3)
        }
}
