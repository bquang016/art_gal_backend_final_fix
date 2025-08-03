package com.example.art_gal.payload;

import lombok.Data;

@Data
public class PaintingSimpleDto {
    private Long id;
    private String name;
    private String artistName;
}