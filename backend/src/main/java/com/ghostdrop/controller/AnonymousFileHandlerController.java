package com.ghostdrop.controller;

import com.ghostdrop.responses.CodeResponse;
import com.ghostdrop.upload.FileHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * The FileHandlerController provides end-points to handle the uploading and retrieval of file data.
 * The endpoints associated with it are: "api/v1/ghost-drop/anonymous (POST & GET)
 *
 * @author Kasodariya Deep
 */
@Slf4j
@RestController
@RequestMapping("api/v1/ghost-drop/anonymous")
public class AnonymousFileHandlerController {

    @Autowired
    private FileHandler fileHandler;

    @Value("${folder.anonymous}")
    private String folderName;

    /**
     * uploadFile uploads the files provided through the request.
     *
     * @param multipartFiles It represents the files given through the request.
     * @return It returns the code associated with the uploaded files.
     */
    @PostMapping
    public ResponseEntity<CodeResponse> uploadFile(@RequestParam("files") MultipartFile[] multipartFiles) {
        log.info("API endpoint /ghost-drop/anonymous: Method:POST");

        CodeResponse code = this.fileHandler.upload(multipartFiles, folderName);
        return new ResponseEntity<>(code, HttpStatus.CREATED);
    }

    /**
     * getZipFile returns the zip file containing all files associated with the code.
     *
     * @param uniqueCode The code that is provided as the query parameter.
     * @return It returns the zip file as a downloadable resource.
     */
    @GetMapping
    public ResponseEntity<Resource> getZipFile(@RequestParam("code") String uniqueCode) {
        log.info("API endpoint /ghost-drop/anonymous: Method:GET");

        // retrieve the path to the zip file.
        Path zipFilePath = fileHandler.getFiles(uniqueCode);

        try {
            // create a resource for the zip file.
            Resource resource = new UrlResource(zipFilePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // return the zip file as a downloadable response.
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFilePath.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                log.error("Zip file not found or not readable: {}", zipFilePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error while downloading zip file for code {}: {}", uniqueCode, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
