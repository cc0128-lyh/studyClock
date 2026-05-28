package com.studyclock.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadsAbsPath = Paths.get("uploads/").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/wallpapers/**")
                .addResourceLocations("file:wallpapers/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsAbsPath);
    }
}
