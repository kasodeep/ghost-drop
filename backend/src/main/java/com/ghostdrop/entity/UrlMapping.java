package com.ghostdrop.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "url_mapping")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uniqueCode;

    @ElementCollection
    @CollectionTable(name = "url_list", joinColumns = @JoinColumn(name = "url_mapping_id"))
    @Column(name = "url")
    private List<String> urls;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
