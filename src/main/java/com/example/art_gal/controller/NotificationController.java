package com.example.art_gal.controller;

import com.example.art_gal.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint để frontend đăng ký push token.
     * @param payload Body của request, chứa key "token".
     * @return Phản hồi thành công.
     */
    @PostMapping("/register-token")
    @PreAuthorize("isAuthenticated()") // Yêu cầu người dùng phải đăng nhập
    public ResponseEntity<?> registerToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        System.out.println("DEBUG LOG: Received token registration request: " + token);
        notificationService.saveUserToken(token);
        return ResponseEntity.ok("Token registered successfully");
    }
}