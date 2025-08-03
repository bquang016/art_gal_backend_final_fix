package com.example.art_gal.service.impl;

import com.example.art_gal.entity.Artist;
import com.example.art_gal.entity.Category;
import com.example.art_gal.entity.Painting;
import com.example.art_gal.entity.User;
import com.example.art_gal.exception.ResourceNotFoundException;
import com.example.art_gal.payload.PaintingDto;
import com.example.art_gal.repository.ArtistRepository;
import com.example.art_gal.repository.CategoryRepository;
import com.example.art_gal.repository.PaintingRepository;
import com.example.art_gal.repository.UserRepository;
import com.example.art_gal.service.ActivityLogService;
import com.example.art_gal.service.NotificationService; // ✅ THÊM DÒNG NÀY
import com.example.art_gal.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired; // ✅ THÊM DÒNG NÀY
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaintingServiceImpl implements PaintingService {

    private final PaintingRepository paintingRepository;
    private final ArtistRepository artistRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityLogService activityLogService;
    private final UserRepository userRepository;

    @Autowired // ✅ THÊM DÒNG NÀY
    private NotificationService notificationService; // ✅ THÊM DÒNG NÀY

    public PaintingServiceImpl(PaintingRepository paintingRepository,
                               ArtistRepository artistRepository,
                               CategoryRepository categoryRepository,
                               ActivityLogService activityLogService,
                               UserRepository userRepository) {
        this.paintingRepository = paintingRepository;
        this.artistRepository = artistRepository;
        this.categoryRepository = categoryRepository;
        this.activityLogService = activityLogService;
        this.userRepository = userRepository;
    }
    
    private User getActor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", 0));
    }

    @Override
    public PaintingDto createPainting(PaintingDto paintingDto) {
        Painting painting = mapToEntity(paintingDto);
        Painting newPainting = paintingRepository.save(painting);
        return mapToDTO(newPainting);
    }

    @Override
    public List<PaintingDto> getAllPaintings() {
        return paintingRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PaintingDto getPaintingById(long id) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Painting", "id", id));
        return mapToDTO(painting);
    }

    @Override
    public PaintingDto updatePainting(PaintingDto paintingDto, long id) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Painting", "id", id));

        Artist artist = artistRepository.findById(paintingDto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", paintingDto.getArtistId()));
        Category category = categoryRepository.findById(paintingDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", paintingDto.getCategoryId()));

        painting.setName(paintingDto.getName());
        painting.setMaterial(paintingDto.getMaterial());
        painting.setImage(paintingDto.getImage());
        painting.setSize(paintingDto.getSize());
        painting.setDescription(paintingDto.getDescription());
        painting.setImportPrice(paintingDto.getImportPrice());
        painting.setSellingPrice(paintingDto.getSellingPrice());
        painting.setStatus(paintingDto.getStatus());
        painting.setArtist(artist);
        painting.setCategory(category);

        Painting updatedPainting = paintingRepository.save(painting);
        
        User actor = getActor();
        String details = String.format("Đã cập nhật thông tin cho tranh '%s' (ID: %d).", updatedPainting.getName(), updatedPainting.getId());
        activityLogService.logActivity(actor, "CẬP NHẬT TRANH", details);
        
        // ✅ GỬI THÔNG BÁO CHO TẤT CẢ ADMIN (TRỪ NGƯỜI THỰC HIỆN)
        userRepository.findAll().stream()
            .filter(adminUser -> !adminUser.getUsername().equals(actor.getUsername()) && adminUser.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName())))
            .forEach(admin -> {
                String title = "Sản phẩm được cập nhật";
                String body = String.format("%s vừa cập nhật thông tin tranh '%s'.", actor.getName(), updatedPainting.getName());
                notificationService.sendNotification(admin.getPushToken(), title, body);
            });

        return mapToDTO(updatedPainting);
    }

    @Override
    public void deletePainting(long id) {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Painting", "id", id));
        paintingRepository.delete(painting);
    }

    private PaintingDto mapToDTO(Painting painting){
        PaintingDto paintingDto = new PaintingDto();
        paintingDto.setId(painting.getId());
        paintingDto.setName(painting.getName());
        paintingDto.setMaterial(painting.getMaterial());
        paintingDto.setImage(painting.getImage());
        paintingDto.setSize(painting.getSize());
        paintingDto.setDescription(painting.getDescription());
        paintingDto.setImportPrice(painting.getImportPrice());
        paintingDto.setSellingPrice(painting.getSellingPrice());
        paintingDto.setStatus(painting.getStatus());
        paintingDto.setArtistId(painting.getArtist().getId());
        paintingDto.setCategoryId(painting.getCategory().getId());
        return paintingDto;
    }

    private Painting mapToEntity(PaintingDto paintingDto){
        Painting painting = new Painting();
        
        Artist artist = artistRepository.findById(paintingDto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", paintingDto.getArtistId()));
        Category category = categoryRepository.findById(paintingDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", paintingDto.getCategoryId()));

        painting.setName(paintingDto.getName());
        painting.setMaterial(paintingDto.getMaterial());
        painting.setImage(paintingDto.getImage());
        painting.setSize(paintingDto.getSize());
        painting.setDescription(paintingDto.getDescription());
        painting.setImportPrice(paintingDto.getImportPrice());
        painting.setSellingPrice(paintingDto.getSellingPrice());
        painting.setStatus(paintingDto.getStatus());
        painting.setArtist(artist);
        painting.setCategory(category);
        return painting;
    }
}