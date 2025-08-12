package ru.itpark.lessons.kafkatemplate.model

data class Order(
    val id: String,
    val item: String,
    val quantity: Int,
    val price: Double,
)
