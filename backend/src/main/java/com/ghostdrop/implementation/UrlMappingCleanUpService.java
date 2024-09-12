package com.ghostdrop.implementation;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.repository.UrlMappingRepository;
import com.ghostdrop.upload.FileHandler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UrlMappingCleanUpService {

    @Autowired
    private UrlMappingRepository mappingRepository;

    @Autowired
    private FileHandler fileHandler;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deleteExpiredUrlMappings() {
        List<UrlMapping> expiredMappings = mappingRepository.findByExpiryDateBefore(LocalDateTime.now());
        if (!expiredMappings.isEmpty()) {
            for (UrlMapping mapping : expiredMappings) {
                for (String secureUrl : mapping.getUrls()) {
                    fileHandler.delete(secureUrl);
                }
                mappingRepository.delete(mapping);
            }

            System.out.println("Cleaned up");
        }
    }
}
