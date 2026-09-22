package com.readMe.controller;

import com.readMe.dto.BookDetailDTO;
import com.readMe.dto.BookRequest;
import com.readMe.dto.BookSummaryDTO;
import com.readMe.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookSummaryDTO>> getAllBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(bookService.getBooksByCategory(category));
        }
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(bookService.searchBooks(search));
        }
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BookDetailDTO> getBookBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(bookService.getBookBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<BookDetailDTO> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

