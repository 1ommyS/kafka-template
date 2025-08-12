package ru.itpark.lessons.kafkatemplate.serializers

import com.example.kafkademo.proto.Person
import org.apache.kafka.common.serialization.Deserializer

class PersonDeserializer : Deserializer<Person> {
    override fun deserialize(
        topic: String?,
        data: ByteArray?,
    ): Person? = data?.let { Person.parseFrom(it) }
}
