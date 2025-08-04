package com.example.art_gal.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${app.firebase-service-account-key-path}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount;

        // Nếu đường dẫn được cấu hình (khi chạy trên server), đọc file từ đường dẫn đó.
        // Ngược lại (khi dev local), vẫn đọc từ classpath như cũ để không ảnh hưởng dev.
        if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
            System.out.println("Loading Firebase service account from path: " + serviceAccountKeyPath);
            serviceAccount = new FileInputStream(serviceAccountKeyPath);
        } else {
            System.out.println("Loading Firebase service account from classpath: serviceAccountKey.json");
            serviceAccount = new ClassPathResource("serviceAccountKey.json").getInputStream();
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}