package ru.itpark.lessons.kafkatemplate.config

import com.example.kafkademo.proto.Person
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import ru.itpark.lessons.kafkatemplate.serializers.PersonDeserializer
import ru.itpark.lessons.kafkatemplate.serializers.PersonSerializer

@Configuration
class KafkaProtoConfig {
    private val bootstrapServers: String =
        System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"

    // ---------- PRODUCER (PROTO) ----------
    @Bean
    fun protoProducerFactory(): ProducerFactory<String, Person> {
        val props =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to PersonSerializer::class.java,
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
            )
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun protoKafkaTemplate(): KafkaTemplate<String, Person> = KafkaTemplate(protoProducerFactory())

    // ---------- CONSUMER (PROTO) ----------
    @Bean
    fun protoConsumerFactory(): ConsumerFactory<String, Person> {
        val props =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to "demo-proto-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to PersonDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            )
        return DefaultKafkaConsumerFactory(props, StringDeserializer(), PersonDeserializer())
    }

    @Bean
    fun protoKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Person> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Person>()
        factory.consumerFactory = protoConsumerFactory()
        factory.setConcurrency(3)
        return factory
    }
}
