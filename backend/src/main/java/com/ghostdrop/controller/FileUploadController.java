package com.ghostdrop.controller;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.upload.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/ghost-drop")
public class FileUploadController {

    @Autowired
    private FileHandler fileHandler;

    @PostMapping
    public ResponseEntity<String> uploadFile(
            @RequestParam("files") MultipartFile[] multipartFiles
    ) {
        String code = this.fileHandler.upload(multipartFiles, "anonymous");
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UrlMapping> getFileUrls(@RequestParam("code") String uniqueCode) {
        UrlMapping urlMapping = fileHandler.getFiles(uniqueCode);
        return new ResponseEntity<>(urlMapping, HttpStatus.OK);
    }
}
