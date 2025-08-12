package ru.itpark.lessons.kafkatemplate.serializers

import com.example.kafkademo.proto.Person
import org.apache.kafka.common.serialization.Serializer

class PersonSerializer : Serializer<Person> {
    override fun serialize(topic: String?, data: Person?): ByteArray? =
        data?.toByteArray()
}
