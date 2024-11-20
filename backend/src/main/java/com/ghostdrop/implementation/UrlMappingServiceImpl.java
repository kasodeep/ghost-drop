package com.ghostdrop.implementation;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.exceptions.TimeExpiredException;
import com.ghostdrop.repository.UrlMappingRepository;
import com.ghostdrop.services.UrlMappingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Concrete implementation of the UrlMappingService.
 *
 * @author Kasodariya Deep
 */
@Service
public class UrlMappingServiceImpl implements UrlMappingService {

    @Autowired
    UrlMappingRepository mappingRepository;

    @Override
    public UrlMapping save(String uniqueCode, List<String> urls) {
        UrlMapping urlMapping = UrlMapping
                .builder()
                .uniqueCode(uniqueCode)
                .urls(urls)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();

        mappingRepository.save(urlMapping);
        return urlMapping;
    }

    @Override
    public UrlMapping get(String uniqueCode) {
        UrlMapping byUniqueCode = mappingRepository
                .findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + uniqueCode));

        if (byUniqueCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TimeExpiredException(String.format("The code %s has already expired", uniqueCode));
        }
        return byUniqueCode;
    }
}
