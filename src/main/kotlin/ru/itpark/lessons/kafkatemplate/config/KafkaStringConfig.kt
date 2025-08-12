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
class KafkaStringConfig {
    private val bootstrapServers: String =
        System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"

    @Bean
    fun stringProducerFactory(): ProducerFactory<String, String> {
        val props =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024, // 32KB
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
            )
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun stringKafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(stringProducerFactory())

    @Bean
    fun stringConsumerFactory(): ConsumerFactory<String, String> {
        val props =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to "demo-string-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun stringKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = stringConsumerFactory()
        factory.setConcurrency(3)
        return factory
    }
}
