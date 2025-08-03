package com.example.art_gal.repository;

import com.example.art_gal.entity.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaintingRepository extends JpaRepository<Painting, Long> {
    
    boolean existsByNameAndArtistId(String name, Long artistId);
    
    long countByStatus(String status);

    long countByCategoryId(Long categoryId);
    
    List<Painting> findAllByCategoryId(Long categoryId);
}