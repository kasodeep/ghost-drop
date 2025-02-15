package com.ghostdrop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The UrlMapping class represents a mapping entity that stores information
 * about unique URL codes and associated URLs with an expiration date. <br>
 * It is used to store a list of URLs mapped to a unique code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "url_mapping")
public class UrlMapping {

    @Id
    @Column(unique = true, nullable = false)
    private String uniqueCode;

    @ElementCollection
    @CollectionTable(name = "url_list", joinColumns = @JoinColumn(name = "url_mapping_id"))
    @Column(name = "url")
    private List<String> urls;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
