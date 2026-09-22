package com.readMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDetailDTO {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String coverImageUrl;
    private Integer chapterNumber;
    private Integer totalPages;
    private String volumeTitle;
    private String bookTitle;
    private String author;
    private List<PageDTO> pages;
}