package ru.itpark.lessons.kafkatemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaTemplateApplication

fun main(args: Array<String>) {
    runApplication<KafkaTemplateApplication>(*args)
}
