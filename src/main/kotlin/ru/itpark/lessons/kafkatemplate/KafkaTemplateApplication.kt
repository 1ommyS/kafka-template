package ru.itpark.lessons.kafkatemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class KafkaTemplateApplication

fun main(args: Array<String>) {
    runApplication<KafkaTemplateApplication>(*args)
}
