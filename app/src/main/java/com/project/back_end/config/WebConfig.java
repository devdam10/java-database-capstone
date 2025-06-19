package com.project.back_end.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; 

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up CORS (Cross-Origin Resource Sharing) in the application.
 * This allows the backend to accept requests from different origins, which is useful for frontend-backend communication.
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Allow CORS for all endpoints
        registry.addMapping("/**")
                .allowedOrigins("*")  // Add your frontend URL here
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Specify allowed methods
                .allowedHeaders("*");  // You can restrict headers if needed
    }
}
