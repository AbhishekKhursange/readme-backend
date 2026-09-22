package com.readMe.service;

import com.readMe.dto.BookDetailDTO;
import com.readMe.dto.BookRequest;
import com.readMe.dto.BookSummaryDTO;

import java.util.List;

public interface BookService {
    List<BookSummaryDTO> getAllBooks();
    List<BookSummaryDTO> getBooksByCategory(String categorySlug);
    List<BookSummaryDTO> searchBooks(String query);
    BookDetailDTO getBookBySlug(String slug);
    BookDetailDTO createBook(BookRequest request);
    void deleteBook(Long id);
}
