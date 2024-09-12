package com.ghostdrop.repository;

import com.ghostdrop.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * UrlMappingRepository acts as an interface to interact with the database.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    /**
     * findByUniqueCode returns the UrlMapping associated with the code provided in the args.
     */
    Optional<UrlMapping> findByUniqueCode(String uniqueCode);

    /**
     * findByExpiryDateBefore returns the list of UrlMappings that have expiry date less than now.
     */
    List<UrlMapping> findByExpiryDateBefore(LocalDateTime now);
}
