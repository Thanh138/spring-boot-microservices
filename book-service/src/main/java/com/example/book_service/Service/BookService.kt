package com.example.book_service.Service

import com.example.book_service.Model.Book
import com.example.book_service.Repository.BookRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class BookService(private val bookRepository: BookRepository) {

    fun getAllBooks(): List<Book> = bookRepository.findAll()

    fun getBookById(id: Long): Optional<Book> = bookRepository.findById(id)

    fun findBooksByTitle(title: String): List<Book> =
        bookRepository.findByTitleContainingIgnoreCase(title)

    fun findBooksByAuthor(author: String): List<Book> =
        bookRepository.findByAuthorContainingIgnoreCase(author)

    fun existsByIsbn(isbn: String): Boolean =
        bookRepository.existsByIsbn(isbn)

    fun findBooksByYearRange(startYear: Int, endYear: Int): List<Book> =
        bookRepository.findBooksByYearRange(startYear, endYear)

    fun findBooksByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Book> =
        bookRepository.findBooksByPriceRange(minPrice, maxPrice)

    fun saveBook(book: Book): Book = bookRepository.save(book)

    fun updateBook(id: Long, updatedBook: Book): Optional<Book> {
        return bookRepository.findById(id).map {
            val bookToUpdate = it.copy(
                title = updatedBook.title,
                author = updatedBook.author,
                price = updatedBook.price,
                isbn = updatedBook.isbn,
                publishedYear = updatedBook.publishedYear,
            )
            bookRepository.save(bookToUpdate)
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
}
