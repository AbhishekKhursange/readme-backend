package com.readMe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookRequest {

    @NotBlank
    private String title;

    private String author;

    private String description;

    private String coverImageUrl;

    @NotNull
    private Long categoryId;

    private Integer recommendedAgeMin;
    private Integer recommendedAgeMax;

    @Valid
    private List<PageRequest> pages;

    @Data
    public static class PageRequest {
        @NotNull
        private Integer pageNumber;

        private String text;

        private String imageUrl;
    }
    
    private List<VolumeRequest> volumes;

    @Data
    public static class VolumeRequest {
        @NotNull
        private Integer volumeNumber;

        @NotBlank
        private String title;

        private String coverImageUrl;

        @Valid
        private List<ChapterRequest> chapters;
    }

    @Data
    public static class ChapterRequest {
        @NotNull
        private Integer chapterNumber;

        @NotBlank
        private String title;

        private String description;

        private String coverImageUrl;

        @Valid
        private List<PageRequest> pages;
    }
}

