package com.readMe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer pageNumber;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;

    // High-res illustration URL stored in Cloudinary
    private String imageUrl;

    // Exactly one of book/chapter is set — a page belongs directly to a
    // standalone book, OR to a chapter (for books organized into
    // volumes/chapters), never both.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
}