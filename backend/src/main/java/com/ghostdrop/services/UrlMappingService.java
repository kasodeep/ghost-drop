package com.ghostdrop.services;

import com.ghostdrop.entity.UrlMapping;

import java.util.List;

/**
 * UrlMappingService is the class associated to service the UrlMapping entity.
 *
 * @author Kasodariya Deep
 */
public interface UrlMappingService {

    /**
     * The save method takes a unique code and a list of urls. <br>
     * It stores the UrlMapping entity in the database.
     */
    UrlMapping save(String uniqueCode, List<String> urls);

    /**
     * The get method takes the unique code. <br>
     * It retrieves the UrlMapping associated with the code.
     */
    UrlMapping get(String uniqueCode);
}
