package ru.itpark.lessons.kafkatemplate.config

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.AdminClientConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class CommonKafkaConfig(
    private val props: KafkaAppProperties,
) {
    @Bean
    fun kafkaAdmin(): KafkaAdmin = KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to props.bootstrapServers))

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun validateTopics() {
        val yaml =
            listOf(
                props.consumers.strings.topic,
                props.consumers.json.topic,
                props.consumers.proto.topic,
                props.producers.strings.topic,
                props.producers.json.topic,
                props.producers.proto.topic,
            ).toSet()

        val code = setOf(Topics.STRING_TOPIC, Topics.JSON_TOPIC, Topics.PROTO_TOPIC)

        if (yaml.containsAll(code)) {
            log.info("Kafka topics in YAML match Topics constants: {}", code)
        } else {
            log.warn("YAML topics {} do not fully match code constants {} — проверь конфиг.", yaml, code)
        }
    }
}
