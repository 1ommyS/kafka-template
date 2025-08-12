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
class KafkaJsonConfig {
    private val bootstrapServers: String =
        System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"

    @Bean
    fun jsonProducerFactory(): ProducerFactory<String, Order> {
        val props =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
            )
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun jsonKafkaTemplate(): KafkaTemplate<String, Order> = KafkaTemplate(jsonProducerFactory())

    // ---------- CONSUMER (JSON) ----------
    @Bean
    fun jsonConsumerFactory(): ConsumerFactory<String, Order> {
        val deserializer =
            JsonDeserializer(Order::class.java).apply {
                addTrustedPackages("*") // демо-режим, в проде — ограничить пакетами
            }
        val props =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to "demo-json-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to deserializer.javaClass,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            )
        return DefaultKafkaConsumerFactory(props, StringDeserializer(), deserializer)
    }

    @Bean
    fun jsonKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Order> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Order>()
        factory.consumerFactory = jsonConsumerFactory()
        factory.setConcurrency(3)
        return factory
    }
}
