package com.readMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailDTO {
    private Long id;
    private String title;
    private String slug;
    private String author;
    private String description;
    private String coverImageUrl;
    private Integer totalPages;
    private String categoryName;
    private String categorySlug;
    private List<PageDTO> pages;
}
