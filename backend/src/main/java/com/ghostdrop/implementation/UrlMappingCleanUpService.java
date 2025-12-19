package com.ghostdrop.implementation;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.repository.UrlMappingRepository;
import com.ghostdrop.handler.FileHandler;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CleanUp service uses the cron job and cleans the expired urls and files.
 * It cleans both the cloud files and the database.
 */
@Component
@Slf4j
public class UrlMappingCleanUpService {

    @Autowired
    private UrlMappingRepository mappingRepository;

    @Autowired
    private FileHandler fileHandler;

    /**
     * Function that runs every minute to check for the expired urls.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deleteExpiredUrlMappings() {
        log.info("Running the Clean Up Service!!");
        List<UrlMapping> expiredMappings = this.mappingRepository.findByExpiryDateBefore(LocalDateTime.now());

        if (!expiredMappings.isEmpty()) {
            // Iterating over the expiredMappings.
            for (UrlMapping mapping : expiredMappings) {
                fileHandler.delete(mapping.getUniqueCode(), mapping.getUrls());
                mappingRepository.delete(mapping);
            }
        }

        log.warn("{} mappings have been cleaned!", expiredMappings.size());
    }
}
