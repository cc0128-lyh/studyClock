package com.studyclock.web.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${studyclock.home:./}")
    private String studyclockHome;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String wallpaperAbs = Paths.get(studyclockHome, "wallpapers").toAbsolutePath().toUri().toString();
        String uploadsAbs = Paths.get(studyclockHome, "uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/wallpapers/**")
                .addResourceLocations(wallpaperAbs);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsAbs);
    }

    @PostConstruct
    public void initDirectories() {
        try {
            Files.createDirectories(Paths.get(studyclockHome, "wallpapers"));
            Files.createDirectories(Paths.get(studyclockHome, "uploads", "avatars"));
        } catch (IOException e) {
            System.err.println("Failed to create data directories: " + e.getMessage());
        }
    }
}
