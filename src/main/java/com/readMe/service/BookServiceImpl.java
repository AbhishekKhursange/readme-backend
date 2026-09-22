package com.readMe.service;

import com.readMe.dto.BookDetailDTO;
import com.readMe.dto.BookRequest;
import com.readMe.dto.BookSummaryDTO;
import com.readMe.dto.PageDTO;
import com.readMe.entity.Book;
import com.readMe.entity.Category;
import com.readMe.entity.Page;
import com.readMe.exception.ResourceNotFoundException;
import com.readMe.repository.BookRepository;
import com.readMe.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Predicate;
import com.readMe.entity.Volume;
import com.readMe.entity.Chapter;
import com.readMe.repository.VolumeRepository;
import com.readMe.repository.ChapterRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final VolumeRepository volumeRepository;
    private final ChapterRepository chapterRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookSummaryDTO> getAllBooks() {
        return bookRepository.findAllStandalone().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookSummaryDTO> getBooksByCategory(String categorySlug) {
        return bookRepository.findStandaloneByCategorySlug(categorySlug).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookSummaryDTO> searchBooks(String query) {
        return bookRepository.searchStandaloneByTitle(query).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailDTO getBookBySlug(String slug) {
        Book book = bookRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + slug));
        return toDetailDTO(book);
    }

    @Override
    @Transactional
    public BookDetailDTO createBook(BookRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setSlug(generateSlug(request.getTitle(), bookRepository::existsBySlug));
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setCoverImageUrl(request.getCoverImageUrl());
        book.setCategory(category);
        book.setRecommendedAgeMin(request.getRecommendedAgeMin());
        book.setRecommendedAgeMax(request.getRecommendedAgeMax());

        if (request.getVolumes() != null && !request.getVolumes().isEmpty()) {
            List<Volume> volumes = request.getVolumes().stream().map(v -> {
                Volume volume = new Volume();
                volume.setVolumeNumber(v.getVolumeNumber());
                volume.setTitle(v.getTitle());
                volume.setSlug(generateSlug(v.getTitle(), volumeRepository::existsBySlug));
                volume.setCoverImageUrl(v.getCoverImageUrl());
                volume.setBook(book);

                if (v.getChapters() != null) {
                    List<Chapter> chapters = v.getChapters().stream().map(c -> {
                        Chapter chapter = new Chapter();
                        chapter.setChapterNumber(c.getChapterNumber());
                        chapter.setTitle(c.getTitle());
                        chapter.setSlug(generateSlug(c.getTitle(), chapterRepository::existsBySlug));
                        chapter.setDescription(c.getDescription());
                        chapter.setCoverImageUrl(c.getCoverImageUrl());
                        chapter.setVolume(volume);

                        if (c.getPages() != null) {
                            List<Page> pages = c.getPages().stream().map(p -> {
                                Page page = new Page();
                                page.setPageNumber(p.getPageNumber());
                                page.setText(p.getText());
                                page.setImageUrl(p.getImageUrl());
                                page.setChapter(chapter);
                                return page;
                            }).collect(Collectors.toList());
                            chapter.setPages(pages);
                            chapter.setTotalPages(pages.size());
                        }
                        return chapter;
                    }).collect(Collectors.toList());
                    volume.setChapters(chapters);
                }
                return volume;
            }).collect(Collectors.toList());
            book.setVolumes(volumes);
        } else if (request.getPages() != null) {
            List<Page> pages = request.getPages().stream().map(p -> {
                Page page = new Page();
                page.setPageNumber(p.getPageNumber());
                page.setText(p.getText());
                page.setImageUrl(p.getImageUrl());
                page.setBook(book);
                return page;
            }).collect(Collectors.toList());
            book.setPages(pages);
            book.setTotalPages(pages.size());
        }

        Book saved = bookRepository.save(book);
        return toDetailDTO(saved);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    private String generateSlug(String title, Predicate<String> existsCheck) {
        String base = title.toLowerCase().trim().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
        String slug = base;
        int counter = 1;
        while (existsCheck.test(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

    private BookSummaryDTO toSummaryDTO(Book book) {
        return new BookSummaryDTO(
                book.getId(),
                book.getTitle(),
                book.getSlug(),
                book.getAuthor(),
                book.getCoverImageUrl(),
                book.getTotalPages(),
                book.getCategory().getName(),
                book.getCategory().getSlug()
        );
    }

    private BookDetailDTO toDetailDTO(Book book) {
        List<PageDTO> pageDTOs = book.getPages().stream()
                .map(p -> new PageDTO(p.getId(), p.getPageNumber(), p.getText(), p.getImageUrl()))
                .collect(Collectors.toList());

        return new BookDetailDTO(
                book.getId(),
                book.getTitle(),
                book.getSlug(),
                book.getAuthor(),
                book.getDescription(),
                book.getCoverImageUrl(),
                book.getTotalPages(),
                book.getCategory().getName(),
                book.getCategory().getSlug(),
                pageDTOs
        );
    }
}

