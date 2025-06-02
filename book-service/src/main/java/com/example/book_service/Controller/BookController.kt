package com.example.book_service.Controller

import com.example.book_service.Dtos.CreateBookRequest
import com.example.book_service.Dtos.UpdateBookRequest
import com.example.book_service.Dtos.BookResponse
import com.example.book_service.Model.BookStatus
import com.example.book_service.Service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/v1/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(bookService.getAllBooks())

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val book = bookService.getBookById(id)
        return if (book.isPresent) ResponseEntity.ok(book.get())
        else ResponseEntity.notFound().build()
    }

    @GetMapping("/available")
    fun getAvailableBooks(): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(bookService.findAvailableBooks())

    @GetMapping("/out-of-stock")
    fun getOutOfStockBooks(): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(bookService.findOutOfStockBooks())

    @GetMapping("/status/{status}")
    fun getBooksByStatus(@PathVariable status: BookStatus): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(bookService.findBooksByStatus(status))

    @GetMapping("/category/{categoryId}")
    fun getBooksByCategory(@PathVariable categoryId: Long): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(bookService.findBooksByCategory(categoryId))

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) yearFrom: Int?,
        @RequestParam(required = false) yearTo: Int?,
        @RequestParam(required = false) status: BookStatus?
    ): ResponseEntity<List<BookResponse>> {
        var results: List<BookResponse> = bookService.getAllBooks()

        title?.let {
            results = results.intersect(bookService.findBooksByTitle(it).toSet()).toList()
        }

        author?.let {
            results = results.intersect(bookService.findBooksByAuthor(it).toSet()).toList()
        }

        if (minPrice != null && maxPrice != null) {
            results = results.intersect(bookService.findBooksByPriceRange(minPrice, maxPrice).toSet()).toList()
        }

        if (yearFrom != null && yearTo != null) {
            results = results.intersect(bookService.findBooksByYearRange(yearFrom, yearTo).toSet()).toList()
        }

        status?.let {
            results = results.intersect(bookService.findBooksByStatus(it).toSet()).toList()
        }

        return ResponseEntity.ok(results)
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createBook(@Valid @RequestBody request: CreateBookRequest): ResponseEntity<Any> {
        return try {
            val book = bookService.createBook(request)
            ResponseEntity.status(HttpStatus.CREATED).body(book)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateBook(
        @PathVariable id: Long, 
        @Valid @RequestBody request: UpdateBookRequest
    ): ResponseEntity<Any> {
        return try {
            val updated = bookService.updateBook(id, request)
            if (updated.isPresent) ResponseEntity.ok(updated.get())
            else ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/{id}/borrow")
    @PreAuthorize("hasRole('ADMIN')")
    fun borrowBook(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val updated = bookService.borrowBook(id)
            if (updated.isPresent) ResponseEntity.ok(updated.get())
            else ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    fun returnBook(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val updated = bookService.returnBook(id)
            if (updated.isPresent) ResponseEntity.ok(updated.get())
            else ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Void> {
        return if (bookService.deleteBook(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }
}