package com.example.book_service.Model

import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.math.BigDecimal

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotBlank(message = "Tiêu đề không được trống")
    @field:Size(min = 1, max = 200, message = "Tiêu đề phải từ 1-200 ký tự")
    @Column(name = "title", nullable = false)
    var title: String,

    @field:NotBlank(message = "Tác giả không được trống")
    val author: String,

    @field:Pattern(regexp = "^\\d{10}(\\d{3})?$", message = "ISBN phải có 10 hoặc 13 chữ số")
    @Column(unique = true)
    val isbn: String,

    @field:Min(value = 1000,  message = "Năm xuất bản phải từ 1000 trở lên")
    @field:Max(value = 9999,  message = "Năm xuất bản phải bé hơn 9999 trở lên")
    val publishedYear: Int,

    @field:NotBlank(message = "Description cannot be blank")
    @field:Size(min = 1, max = 200, message = "Description must be between 1 and 200 characters")
    @Column(columnDefinition = "TEXT")
    val description: String,

    @field:NotNull(message = "copies can not be null")
    @field:PositiveOrZero(message = "copies must be greater than or equal to zero")
    val totalCopies: Int = 0,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @field:Digits(integer = 10, fraction = 2, message = "Giá không hợp lệ")
    val price: BigDecimal,

    @field:PositiveOrZero(message = "Available copies must be greater than or equal to zero")
    val availableCopies: Int,

    @Enumerated(EnumType.STRING)
    val status: BookStatus = BookStatus.AVAILABLE,

    @ElementCollection
    @CollectionTable(name = "book_categories", joinColumns = [JoinColumn(name = "book_id")])
    @Column(name = "category_id")
    val categoryIds: MutableList<Long> = mutableListOf()
) {
    constructor() : this(
        id = 0,
        title = "",
        author = "",
        isbn = "",
        publishedYear = 1000,
        description = "",
        totalCopies = 0,
        price = BigDecimal("0.01"),
        availableCopies = 0,
        status = BookStatus.AVAILABLE,
        categoryIds = mutableListOf()
    )
}