package com.readMe.repository;

import com.readMe.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findBySlug(String slug);
    boolean existsBySlug(String slug);
}