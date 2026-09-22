package com.readMe.repository;

import com.readMe.entity.Volume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolumeRepository extends JpaRepository<Volume, Long> {
    Optional<Volume> findBySlug(String slug);
    List<Volume> findAllByOrderByVolumeNumberAsc();
    boolean existsBySlug(String slug);
}