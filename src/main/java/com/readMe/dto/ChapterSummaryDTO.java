package com.readMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterSummaryDTO {
    private Long id;
    private String title;
    private String slug;
    private String coverImageUrl;
    private Integer chapterNumber;
    private Integer totalPages;
}