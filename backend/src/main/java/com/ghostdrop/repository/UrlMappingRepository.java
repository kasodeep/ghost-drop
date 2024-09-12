package com.ghostdrop.repository;

import com.ghostdrop.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByUniqueCode(String uniqueCode);

    List<UrlMapping> findByExpiryDateBefore(LocalDateTime now);
}
