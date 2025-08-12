package ru.itpark.lessons.kafkatemplate.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*

@Configuration
class KafkaStringConfig(
    private val app: KafkaAppProperties,
) {
    // ---------- PRODUCER (String) ----------
    @Bean
    fun stringProducerFactory(): ProducerFactory<String, String> {
        val p =
            mutableMapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to app.bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            )
        app.schemaRegistryUrl?.let { p["schema.registry.url"] = it } // на будущее
        return DefaultKafkaProducerFactory(p)
    }

    @Bean
    fun stringKafkaTemplate(): KafkaTemplate<String, String> =
        KafkaTemplate(stringProducerFactory()).apply {
            // defaultTopic не обязателен, но удобно
            defaultTopic = app.producers.strings.topic
        }

    @Bean
    fun stringConsumerFactory(): ConsumerFactory<String, String> {
        val c =
            mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to app.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to
                    app.consumers.strings.groupId
                        .ifBlank { "demo-string-group" },
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to app.consumers.common.autoOffsetReset,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to app.consumers.common.autoCommit,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            )
        app.schemaRegistryUrl?.let { c["schema.registry.url"] = it }
        return DefaultKafkaConsumerFactory(c)
    }

    @Bean(name = ["stringKafkaListenerContainerFactory"])
    fun stringKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = stringConsumerFactory()
            setConcurrency(3)
        }
}
