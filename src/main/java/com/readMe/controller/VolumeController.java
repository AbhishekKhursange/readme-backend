package com.readMe.controller;

import com.readMe.dto.ChapterSummaryDTO;
import com.readMe.dto.VolumeDetailDTO;
import com.readMe.dto.VolumeSummaryDTO;
import com.readMe.entity.Volume;
import com.readMe.exception.ResourceNotFoundException;
import com.readMe.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volumes")
@RequiredArgsConstructor
public class VolumeController {

    private final VolumeRepository volumeRepository;

    @GetMapping
    public ResponseEntity<List<VolumeSummaryDTO>> getAllVolumes() {
        List<VolumeSummaryDTO> volumes = volumeRepository.findAllByOrderByVolumeNumberAsc().stream()
                .map(this::toSummaryDTO)
                .toList();
        return ResponseEntity.ok(volumes);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<VolumeDetailDTO> getVolumeBySlug(@PathVariable String slug) {
        Volume volume = volumeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Volume not found: " + slug));

        List<ChapterSummaryDTO> chapters = volume.getChapters().stream()
                .map(c -> new ChapterSummaryDTO(c.getId(), c.getTitle(), c.getSlug(), c.getCoverImageUrl(), c.getChapterNumber(), c.getTotalPages()))
                .toList();

        VolumeDetailDTO dto = new VolumeDetailDTO(
                volume.getId(),
                volume.getTitle(),
                volume.getSlug(),
                volume.getCoverImageUrl(),
                volume.getVolumeNumber(),
                volume.getBook().getTitle(),
                chapters
        );
        return ResponseEntity.ok(dto);
    }

    private VolumeSummaryDTO toSummaryDTO(Volume volume) {
        return new VolumeSummaryDTO(
                volume.getId(),
                volume.getTitle(),
                volume.getSlug(),
                volume.getCoverImageUrl(),
                volume.getVolumeNumber(),
                volume.getBook().getTitle(),
                volume.getBook().getCategory().getName(),
                volume.getBook().getCategory().getSlug(),
                volume.getChapters().size()
        );
    }
}