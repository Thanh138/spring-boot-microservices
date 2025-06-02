package com.example.book_service.Service

import com.example.book_service.Dtos.CreateBookRequest
import com.example.book_service.Dtos.UpdateBookRequest
import com.example.book_service.Dtos.BookResponse
import com.example.book_service.Model.Book
import com.example.book_service.Model.BookStatus
import com.example.book_service.Repository.BookRepository
import com.example.book_service.utils.CategoryClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class BookService(private val bookRepository: BookRepository, private val categoryClient: CategoryClient) {

    fun getAllBooks(): List<BookResponse> = 
        bookRepository.findAll().map { it.toBookResponse() }

    fun getBookById(id: Long): Optional<BookResponse> = 
        bookRepository.findById(id).map { it.toBookResponse() }

    fun findBooksByTitle(title: String): List<BookResponse> =
        bookRepository.findByTitleContainingIgnoreCase(title).map { it.toBookResponse() }

    fun findBooksByAuthor(author: String): List<BookResponse> =
        bookRepository.findByAuthorContainingIgnoreCase(author).map { it.toBookResponse() }

    fun findBooksByStatus(status: BookStatus): List<BookResponse> =
        bookRepository.findByStatus(status).map { it.toBookResponse() }

    fun findAvailableBooks(): List<BookResponse> =
        bookRepository.findByAvailableCopiesGreaterThan(0).map { it.toBookResponse() }

    fun findBooksByCategory(categoryId: Long): List<BookResponse> =
        bookRepository.findBooksByCategoryId(categoryId).map { it.toBookResponse() }

    fun existsByIsbn(isbn: String): Boolean = bookRepository.existsByIsbn(isbn)

    fun findBooksByYearRange(startYear: Int, endYear: Int): List<BookResponse> =
        bookRepository.findBooksByYearRange(startYear, endYear).map { it.toBookResponse() }

    fun findBooksByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<BookResponse> =
        bookRepository.findBooksByPriceRange(minPrice, maxPrice).map { it.toBookResponse() }

    fun findOutOfStockBooks(): List<BookResponse> =
        bookRepository.findOutOfStockBooks().map { it.toBookResponse() }

    fun createBook(request: CreateBookRequest): BookResponse {
        if (existsByIsbn(request.isbn)) {
            throw IllegalArgumentException("Book with ISBN ${request.isbn} already exists")
        }

        if (request.categoryIds.isNotEmpty()) {
            val categoriesValid = categoryClient.validateCategories(request.categoryIds)
            if (!categoriesValid) {
                throw IllegalArgumentException("One or more category IDs are invalid or inactive")
            }
        }

        val book = Book(
            title = request.title,
            author = request.author,
            isbn = request.isbn,
            publishedYear = request.publishedYear,
            description = request.description,
            totalCopies = request.totalCopies,
            availableCopies = request.availableCopies,
            price = request.price,
            status = request.status,
            categoryIds = request.categoryIds.toMutableList()
        )

        return bookRepository.save(book).toBookResponse()
    }


    fun updateBook(id: Long, request: UpdateBookRequest): Optional<BookResponse> {
        return bookRepository.findById(id).map { existingBook ->
            // Validate categories if provided
            request.categoryIds?.let { categoryIds ->
                if (categoryIds.isNotEmpty()) {
                    val categoriesValid = categoryClient.validateCategories(categoryIds)
                    if (!categoriesValid) {
                        throw IllegalArgumentException("One or more category IDs are invalid or inactive")
                    }
                }
            }

            val updatedBook = existingBook.copy(
                title = request.title ?: existingBook.title,
                author = request.author ?: existingBook.author,
                publishedYear = request.publishedYear ?: existingBook.publishedYear,
                description = request.description ?: existingBook.description,
                totalCopies = request.totalCopies ?: existingBook.totalCopies,
                availableCopies = request.availableCopies ?: existingBook.availableCopies,
                price = request.price ?: existingBook.price,
                status = request.status ?: existingBook.status,
                categoryIds = request.categoryIds?.toMutableList() ?: existingBook.categoryIds
            )

            // Validate copies constraint
            if (updatedBook.availableCopies > updatedBook.totalCopies) {
                throw IllegalArgumentException(
                    "Available copies (${updatedBook.availableCopies}) cannot be greater than total copies (${updatedBook.totalCopies})"
                )
            }

            bookRepository.save(updatedBook).toBookResponse()
        }
    }


    fun borrowBook(id: Long): Optional<BookResponse> {
        return bookRepository.findById(id).map { book ->
            if (book.availableCopies <= 0) {
                throw IllegalStateException("No available copies to borrow")
            }

            val updatedBook = book.copy(availableCopies = book.availableCopies - 1)
            bookRepository.save(updatedBook).toBookResponse()
        }
    }

    fun returnBook(id: Long): Optional<BookResponse> {
        return bookRepository.findById(id).map { book ->
            if (book.availableCopies >= book.totalCopies) {
                throw IllegalStateException("All copies are already available")
            }

            val updatedBook = book.copy(availableCopies = book.availableCopies + 1)
            bookRepository.save(updatedBook).toBookResponse()
        }
    }

    fun deleteBook(id: Long): Boolean {
        return if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // Extension function to convert Book to BookResponse
    private fun Book.toBookResponse(): BookResponse {
        return BookResponse(
            id = this.id,
            title = this.title,
            author = this.author,
            isbn = this.isbn,
            publishedYear = this.publishedYear,
            description = this.description,
            totalCopies = this.totalCopies,
            availableCopies = this.availableCopies,
            price = this.price,
            status = this.status,
            categoryIds = this.categoryIds.toList()
        )
    }
}