package com.ghostdrop.services;

import com.ghostdrop.entity.UrlMapping;

import java.util.List;

public interface UrlMappingService {

    UrlMapping save(String uniqueCode, List<String> urls);

    UrlMapping get(String uniqueCode);
}
