package com.ghostdrop.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProjectConfiguration {

    @Bean
    public Cloudinary getCloudinary() {
        Map<Object, Object> config = new HashMap<>();
        // Config removed.
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
