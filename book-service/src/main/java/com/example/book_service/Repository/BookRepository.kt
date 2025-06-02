package com.example.book_service.Repository

import com.example.book_service.Model.Book
import com.example.book_service.Model.BookStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@RepositoryRestResource
@Repository
interface BookRepository : JpaRepository<Book, Long> {
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    fun existsByIsbn(isbn: String): Boolean
    fun findByStatus(status: BookStatus): List<Book>
    fun findByAvailableCopiesGreaterThan(copies: Int): List<Book>

    @Query("SELECT b FROM Book b WHERE b.publishedYear BETWEEN :startYear AND :endYear")
    fun findBooksByYearRange(
        @Param("startYear") startYear: Int,
        @Param("endYear") endYear: Int
    ): List<Book>

    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    fun findBooksByPriceRange(
        @Param("minPrice") minPrice: BigDecimal,
        @Param("maxPrice") maxPrice: BigDecimal
    ): List<Book>

    @Query("SELECT b FROM Book b WHERE :categoryId MEMBER OF b.categoryIds")
    fun findBooksByCategoryId(@Param("categoryId") categoryId: Long): List<Book>

    @Query("SELECT b FROM Book b WHERE b.totalCopies > b.availableCopies")
    fun findBooksWithBorrowedCopies(): List<Book>

    @Query("SELECT b FROM Book b WHERE b.availableCopies = 0")
    fun findOutOfStockBooks(): List<Book>
}