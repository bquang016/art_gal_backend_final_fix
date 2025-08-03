package com.example.art_gal.service;

import com.example.art_gal.entity.User;
import com.example.art_gal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    private final WebClient webClient;

    // Sử dụng WebClient để gửi yêu cầu HTTP đến máy chủ Expo
    public NotificationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://exp.host/--/api/v2").build();
    }

    /**
     * Lưu push token của người dùng đang đăng nhập.
     * @param token Expo Push Token từ frontend.
     */
    public void saveUserToken(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        userRepository.findByUsername(currentUsername).ifPresent(user -> {
            System.out.println("DEBUG LOG: Saving token for user: " + currentUsername);
            user.setPushToken(token);
            userRepository.save(user);
        });
    }

    /**
     * Gửi thông báo đến một token cụ thể thông qua máy chủ của Expo.
     * @param token Token của thiết bị cần gửi (phải là Expo Push Token).
     * @param title Tiêu đề của thông báo.
     * @param body Nội dung của thông báo.
     */
    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            System.out.println("DEBUG LOG: No push token found for user. Skipping notification.");
            return;
        }
        
        if (!token.startsWith("ExponentPushToken")) {
            System.err.println("ERROR: Invalid Expo Push Token: " + token);
            return;
        }

        System.out.println("DEBUG LOG: Attempting to send Expo notification to token: " + token);

        // Tạo body cho request đến Expo API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("to", token);
        requestBody.put("title", title);
        requestBody.put("body", body);

        // Gửi yêu cầu POST bất đồng bộ
        webClient.post()
                .uri("/push/send")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("DEBUG LOG: Expo push API response: " + response))
                .doOnError(error -> System.err.println("ERROR: Failed to send notification via Expo: " + error.getMessage()))
                .subscribe(); // Thực thi request
    }
}