package com.example.book_service.Controller

import com.example.book_service.Model.Book
import com.example.book_service.Service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<Book>> =
        ResponseEntity.ok(bookService.getAllBooks())

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<Book> {
        val book = bookService.getBookById(id)
        return if (book.isPresent) ResponseEntity.ok(book.get())
        else ResponseEntity.notFound().build()
    }

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) yearFrom: Int?,
        @RequestParam(required = false) yearTo: Int?
    ): ResponseEntity<List<Book>> {
        var results: List<Book> = bookService.getAllBooks()

        title?.let {
            results = results.intersect(bookService.findBooksByTitle(it)).toList()
        }

        author?.let {
            results = results.intersect(bookService.findBooksByAuthor(it)).toList()
        }

        if (minPrice != null && maxPrice != null) {
            results = results.intersect(bookService.findBooksByPriceRange(minPrice, maxPrice)).toList()
        }

        if (yearFrom != null && yearTo != null) {
            results = results.intersect(bookService.findBooksByYearRange(yearFrom, yearTo)).toList()
        }

        return ResponseEntity.ok(results)
    }

    @PostMapping
    fun createBook(@RequestBody book: Book): ResponseEntity<Book> {
        if (bookService.existsByIsbn(book.isbn)) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok(bookService.saveBook(book))
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: Long, @RequestBody book: Book): ResponseEntity<Book> {
        val updated = bookService.updateBook(id, book)
        return if (updated.isPresent) ResponseEntity.ok(updated.get())
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Void> {
        return if (bookService.deleteBook(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}