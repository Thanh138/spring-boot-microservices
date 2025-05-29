package com.example.book_service.Dtos

import java.time.LocalDateTime

data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val publishedDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
