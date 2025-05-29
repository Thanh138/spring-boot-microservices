package com.example.book_service.Model

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
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

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @field:Digits(integer = 10, fraction = 2, message = "Giá không hợp lệ")
    val price: BigDecimal,

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    val category: Category? = null,*/
) {
    constructor() : this(0, "", "", "", 0, BigDecimal.ZERO)}