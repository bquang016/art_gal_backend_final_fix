package com.example.art_gal.payload;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDetailDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private List<PaintingSimpleDto> paintings;
}