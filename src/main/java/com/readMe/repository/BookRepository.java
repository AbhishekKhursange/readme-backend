package com.readMe.repository;

import com.readMe.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBySlug(String slug);
    List<Book> findByCategorySlug(String categorySlug);
    List<Book> findByTitleContainingIgnoreCase(String title);
    boolean existsBySlug(String slug);

    // Only books read directly (no volumes) — these are what the home grid
    // shows as regular book cards. Books organized into volumes show their
    // volumes as cards instead (see VolumeRepository/VolumeController).
    @Query("SELECT b FROM Book b WHERE b.volumes IS EMPTY")
    List<Book> findAllStandalone();

    @Query("SELECT b FROM Book b WHERE b.volumes IS EMPTY AND b.category.slug = :categorySlug")
    List<Book> findStandaloneByCategorySlug(@Param("categorySlug") String categorySlug);

    @Query("SELECT b FROM Book b WHERE b.volumes IS EMPTY AND lower(b.title) LIKE lower(concat('%', :query, '%'))")
    List<Book> searchStandaloneByTitle(@Param("query") String query);
}
