package com.readMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeDetailDTO {
    private Long id;
    private String title;
    private String slug;
    private String coverImageUrl;
    private Integer volumeNumber;
    private String bookTitle;
    private List<ChapterSummaryDTO> chapters;
}