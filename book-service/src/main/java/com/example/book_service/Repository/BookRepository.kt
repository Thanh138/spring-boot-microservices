package com.example.book_service.Repository

import com.example.book_service.Model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface BookRepository : JpaRepository<Book, Long> {
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    fun existsByIsbn(isbn: String): Boolean

    @Query("SELECT b FROM Book b WHERE b.publishedYear BETWEEN :startYear AND :endYear")
    fun findBooksByYearRange(
        @Param("startYear") startYear: Int,
        @Param("endYear") endYear: Int
    ): List<Book>

    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    fun findBooksByPriceRange(
        @Param("minPrice") minPrice: java.math.BigDecimal,
        @Param("maxPrice") maxPrice: java.math.BigDecimal
    ): List<Book>
}