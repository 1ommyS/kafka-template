package ru.itpark.lessons.kafkatemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
@ConfigurationPropertiesScan
class KafkaTemplateApplication

fun main(args: Array<String>) {
    runApplication<KafkaTemplateApplication>(*args)
}
