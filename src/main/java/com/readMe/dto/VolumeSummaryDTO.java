package com.readMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeSummaryDTO {
    private Long id;
    private String title;
    private String slug;
    private String coverImageUrl;
    private Integer volumeNumber;
    private String bookTitle;
    private String categoryName;
    private String categorySlug;
    private int chapterCount;
}