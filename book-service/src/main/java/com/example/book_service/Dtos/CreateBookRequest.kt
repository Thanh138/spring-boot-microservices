package com.example.book_service.Dtos

import jakarta.validation.constraints.NotBlank

data class CreateBookRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val author: String
)
