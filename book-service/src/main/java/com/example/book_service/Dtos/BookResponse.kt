package com.example.book_service.Dtos

import com.example.book_service.Model.BookStatus
import java.math.BigDecimal

data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val publishedYear: Int,
    val description: String,
    val totalCopies: Int,
    val availableCopies: Int,
    val price: BigDecimal,
    val status: BookStatus,
    val categoryIds: List<Long>
)
