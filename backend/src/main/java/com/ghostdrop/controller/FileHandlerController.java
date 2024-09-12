package com.ghostdrop.controller;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.responses.CodeResponse;
import com.ghostdrop.services.UrlMappingService;
import com.ghostdrop.upload.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * The FileHandlerController provides end-points to handle the uploading and retrieval of file data.
 * The endpoints associated with it are: "api/v1/ghost-drop/anonymous (POST & GET)
 *
 * @author Kasodariya Deep
 */
@RestController
@RequestMapping("api/v1/ghost-drop/anonymous")
public class FileHandlerController {

    @Autowired
    private FileHandler fileHandler;

    @Autowired
    private UrlMappingService mappingService;

    @Value("${folder.anonymous}")
    private String folderName;

    /**
     * uploadFile uploads the files provided through the request.
     *
     * @param multipartFiles It represents the files given through the request..
     * @return It returns the code associated with the uploaded files.
     */
    @PostMapping
    public ResponseEntity<CodeResponse> uploadFile(@RequestParam("files") MultipartFile[] multipartFiles) {
        CodeResponse code = this.fileHandler.upload(multipartFiles, folderName);
        return new ResponseEntity<>(code, HttpStatus.CREATED);
    }

    /**
     * getFileUrls returns the urls of the files associated with the code.
     *
     * @param uniqueCode The code that is provided as the query parameter.
     * @return It returns the list of file urls uploaded on the cloud.
     */
    @GetMapping
    public ResponseEntity<UrlMapping> getFileUrls(@RequestParam("code") String uniqueCode) {
        UrlMapping urlMapping = this.mappingService.get(uniqueCode);
        return new ResponseEntity<>(urlMapping, HttpStatus.OK);
    }
}
