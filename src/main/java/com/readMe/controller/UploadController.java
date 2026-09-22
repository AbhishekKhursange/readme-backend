package com.readMe.controller;

import com.readMe.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    // folder examples: "covers", "pages/leo-moon-fort-adventure"
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "readMe") String folder) throws IOException {

        String url = cloudinaryService.uploadImage(file, folder);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
