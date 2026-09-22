package com.readMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryDTO {
    private Long id;
    private String title;
    private String slug;
    private String author;
    private String coverImageUrl;
    private Integer totalPages;
    private String categoryName;
    private String categorySlug;
}
