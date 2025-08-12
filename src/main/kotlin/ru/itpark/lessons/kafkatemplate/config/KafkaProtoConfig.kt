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
class KafkaProtoConfig(
    private val app: KafkaAppProperties,
) {
    // ---------- PRODUCER (PROTO) ----------
    @Bean
    fun protoProducerFactory(): ProducerFactory<String, Person> {
        val p =
            mutableMapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to app.bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32 * 1024,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to PersonSerializer::class.java,
            )
        app.schemaRegistryUrl?.let { p["schema.registry.url"] = it }
        return DefaultKafkaProducerFactory(p)
    }

    @Bean
    fun protoKafkaTemplate(): KafkaTemplate<String, Person> =
        KafkaTemplate(protoProducerFactory()).apply {
            defaultTopic = app.producers.proto.topic
        }

    // ---------- CONSUMER (PROTO) ----------
    @Bean
    fun protoConsumerFactory(): ConsumerFactory<String, Person> {
        val c =
            mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to app.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to
                    app.consumers.proto.groupId
                        .ifBlank { "demo-proto-group" },
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to app.consumers.common.autoOffsetReset,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to app.consumers.common.autoCommit,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to PersonDeserializer::class.java,
            )
        app.schemaRegistryUrl?.let { c["schema.registry.url"] = it }
        return DefaultKafkaConsumerFactory(c, StringDeserializer(), PersonDeserializer())
    }

    @Bean(name = ["protoKafkaListenerContainerFactory"])
    fun protoKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Person> =
        ConcurrentKafkaListenerContainerFactory<String, Person>().apply {
            consumerFactory = protoConsumerFactory()
            setConcurrency(3)
        }
}
