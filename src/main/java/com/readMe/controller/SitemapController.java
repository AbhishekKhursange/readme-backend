package com.readMe.controller;

import com.readMe.config.AppUrlProperties;
import com.readMe.repository.BookRepository;
import com.readMe.repository.CategoryRepository;
import com.readMe.repository.ChapterRepository;
import com.readMe.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SitemapController {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final VolumeRepository volumeRepository;
    private final ChapterRepository chapterRepository;
    private final AppUrlProperties appUrlProperties;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {
        String base = appUrlProperties.getFrontendBaseUrl();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        xml.append(url(base));

        categoryRepository.findAll().forEach(c -> xml.append(url(base + "/category/" + c.getSlug())));
        bookRepository.findAllStandalone().forEach(b -> xml.append(url(base + "/book/" + b.getSlug())));
        volumeRepository.findAll().forEach(v -> xml.append(url(base + "/volume/" + v.getSlug())));
        chapterRepository.findAll().forEach(c -> xml.append(url(base + "/chapter/" + c.getSlug())));

        xml.append("</urlset>");

        return ResponseEntity.ok(xml.toString());
    }

    private String url(String loc) {
        return "  <url><loc>" + loc + "</loc></url>\n";
    }
}