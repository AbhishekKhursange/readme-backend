package com.readMe.controller;

import com.readMe.dto.ChapterDetailDTO;
import com.readMe.dto.PageDTO;
import com.readMe.entity.Chapter;
import com.readMe.exception.ResourceNotFoundException;
import com.readMe.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterRepository chapterRepository;

    @GetMapping("/{slug}")
    public ResponseEntity<ChapterDetailDTO> getChapterBySlug(@PathVariable String slug) {
        Chapter chapter = chapterRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found: " + slug));

        List<PageDTO> pages = chapter.getPages().stream()
                .map(p -> new PageDTO(p.getId(), p.getPageNumber(), p.getText(), p.getImageUrl()))
                .toList();

        ChapterDetailDTO dto = new ChapterDetailDTO(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getSlug(),
                chapter.getDescription(),
                chapter.getCoverImageUrl(),
                chapter.getChapterNumber(),
                chapter.getTotalPages(),
                chapter.getVolume().getTitle(),
                chapter.getVolume().getBook().getTitle(),
                chapter.getVolume().getBook().getAuthor(),
                pages
        );
        return ResponseEntity.ok(dto);
    }
}