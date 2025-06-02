package com.example.book_service.Dtos

import com.example.book_service.Model.BookStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class CreateBookRequest(
    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    val title: String,

    @field:NotBlank(message = "Author cannot be blank")
    val author: String,

    @field:Pattern(regexp = "^\\d{10}(\\d{3})?$", message = "ISBN must have 10 or 13 digits")
    val isbn: String,

    @field:Min(value = 1000, message = "Published year must be from 1000 onwards")
    @field:Max(value = 9999, message = "Published year must be less than 9999")
    val publishedYear: Int,

    @field:NotBlank(message = "Description cannot be blank")
    @field:Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    val description: String,

    @field:NotNull(message = "Total copies cannot be null")
    @field:PositiveOrZero(message = "Total copies must be greater than or equal to zero")
    val totalCopies: Int,

    @field:NotNull(message = "Available copies cannot be null")
    @field:PositiveOrZero(message = "Available copies must be greater than or equal to zero")
    val availableCopies: Int,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @field:Digits(integer = 10, fraction = 2, message = "Invalid price format")
    val price: BigDecimal,

    val status: BookStatus = BookStatus.AVAILABLE,

    val categoryIds: List<Long> = emptyList()
) {
    init {
        require(availableCopies <= totalCopies) {
            "Available copies ($availableCopies) cannot be greater than total copies ($totalCopies)"
        }
    }
}