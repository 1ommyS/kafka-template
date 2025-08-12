package ru.itpark.lessons.kafkatemplate

import com.example.kafkademo.proto.Person
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import ru.itpark.lessons.kafkatemplate.config.Topics
import ru.itpark.lessons.kafkatemplate.model.Order
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/send")
class SendController(
    private val stringTemplate: KafkaTemplate<String, String>,
    private val jsonTemplate: KafkaTemplate<String, Order>,
    private val protoTemplate: KafkaTemplate<String, Person>,
    private val stringTopic: NewTopic,
) {
    data class Meta(
        val topic: String,
        val partition: Int,
        val offset: Long,
    )

    @PostMapping("/string")
    fun sendString(
        @RequestParam(required = false) key: String?,
        @RequestBody message: String,
    ): ResponseEntity<Meta> {
        val future =
            key?.let { stringTemplate.send(Topics.STRING_TOPIC, key, message) }
                ?: stringTemplate.send(Topics.STRING_TOPIC, message)
        val meta: RecordMetadata = future.get(5, TimeUnit.SECONDS).recordMetadata
        return ResponseEntity.ok(Meta(meta.topic(), meta.partition(), meta.offset()))
    }

    @PostMapping("/json")
    fun sendJson(
        @RequestParam(required = false) key: String?,
        @RequestBody order: Order,
    ): ResponseEntity<Meta> {
        val future =
            key?.let { jsonTemplate.send(Topics.JSON_TOPIC, key, order) } ?: jsonTemplate.send(Topics.JSON_TOPIC, order)
        val meta: RecordMetadata = future.get(5, TimeUnit.SECONDS).recordMetadata
        return ResponseEntity.ok(Meta(meta.topic(), meta.partition(), meta.offset()))
    }

    data class PersonDto(
        val id: String,
        val name: String,
        val age: Int,
        val email: String,
    )

    @PostMapping("/proto")
    fun sendProto(
        @RequestParam(required = false) key: String?,
        @RequestBody dto: PersonDto,
    ): ResponseEntity<Meta> {
        val person =
            Person
                .newBuilder()
                .setId(dto.id)
                .setName(dto.name)
                .setAge(dto.age)
                .setEmail(dto.email)
                .build()
        val future =
            key?.let { protoTemplate.send(Topics.PROTO_TOPIC, key, person) } ?: protoTemplate.send(
                Topics.PROTO_TOPIC,
                person,
            )
        val meta: RecordMetadata = future.get(5, TimeUnit.SECONDS).recordMetadata
        return ResponseEntity.ok(Meta(meta.topic(), meta.partition(), meta.offset()))
    }
}
