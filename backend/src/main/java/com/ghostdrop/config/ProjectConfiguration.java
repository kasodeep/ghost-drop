package com.ghostdrop.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * ProjectConfiguration class is used to provide or inject bean configurations to different autowired attributes.
 * It primarily configures the Cloudinary service using values from the application properties file.
 *
 * @author Kasodariya Deep
 */
@Configuration
public class ProjectConfiguration {

    // Cloudinary's configuration values injected from application properties.
    @Value("${cloudinary.cloudName}")
    private String cloudName;

    @Value("${cloudinary.apiKey}")
    private String apiKey;

    @Value("${cloudinary.apiSecret}")
    private String apiSecret;

    /**
     * Creates and configures a Cloudinary bean that can be used throughout the application.
     * The configuration includes the cloud name, API key, and API secret which are fetched
     * from the application properties.
     *
     * @return a configured Cloudinary object.
     */
    @Bean
    public Cloudinary getCloudinary() {
        Map<Object, Object> config = new HashMap<>();
        config.put("cloud_name", this.cloudName);
        config.put("api_key", this.apiKey);
        config.put("api_secret", this.apiSecret);

        // Ensures secure HTTPS connections.
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
