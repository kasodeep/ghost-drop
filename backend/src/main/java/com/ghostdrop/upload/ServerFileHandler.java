package com.ghostdrop.upload;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.exceptions.FileDeleteFailedException;
import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.responses.CodeResponse;
import com.ghostdrop.services.UrlMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ServerFileHandler stores files locally on the server and handles upload, retrieval, and deletion.
 *
 * @author Kasodariya
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "file.handler", havingValue = "server")
public class ServerFileHandler implements FileHandler {

    @Value("${base.directory}")
    private String BASE_DIRECTORY;

    @Autowired
    private UrlMappingService mappingService;

    @Override
    public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
        List<String> fileUrls = new ArrayList<>();
        String uniqueCode = generateUniqueCode();

        try {
            System.out.println("Here");
            // create directories for the new files.
            Path folderPath = Paths.get(BASE_DIRECTORY, folderName, uniqueCode);
            Files.createDirectories(folderPath);

            // Save files to the folder.
            for (MultipartFile file : multipartFiles) {
                String fileNameWithExtension = file.getOriginalFilename();
                if (fileNameWithExtension == null) {
                    throw new FileNotFoundException();
                }

                Path filePath = folderPath.resolve(fileNameWithExtension);
                Files.write(filePath, file.getBytes());
                fileUrls.add(filePath.toString());

                log.info("File uploaded with name {}", fileNameWithExtension);
            }

            // save file URLs with the unique code.
            mappingService.save(uniqueCode, fileUrls);
            return new CodeResponse(uniqueCode);

        } catch (IOException e) {
            log.error("Deleting due to error in uploading!");
            delete(uniqueCode, fileUrls);
            throw new FileUploadFailedException("Failed to upload the files!");
        }
    }

    @Override
    public void delete(String uniqueCode, List<String> fileUrls) {
        try {
            // delete each file.
            for (String fileUrl : fileUrls) {
                Path path = Paths.get(fileUrl);
                Files.deleteIfExists(path);
                log.info("File with path {} deleted", fileUrl);
            }

            // delete the code directory.
            Path codePath = Paths.get(BASE_DIRECTORY, "anonymous", uniqueCode);
            Files.deleteIfExists(codePath);

            // delete the associated zip file if it exists
            Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");
            Files.deleteIfExists(zipFilePath);
            log.info("Zip file with path {} deleted", zipFilePath);

        } catch (Exception e) {
            throw new FileDeleteFailedException("Failed to delete files for code: " + uniqueCode);
        }
    }

    @Override
    public Path getFiles(String uniqueCode) {
        try {
            // create the path for the zip file.
            Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");

            // check if the zip file already exists.
            if (Files.exists(zipFilePath)) {
                log.info("Zip file already exists for code {}", uniqueCode);
                return zipFilePath;
            }

            // retrieve file URLs from mappingService using the unique code.
            UrlMapping urlMapping = mappingService.get(uniqueCode);
            List<String> fileUrls = urlMapping.getUrls();

            if (fileUrls == null || fileUrls.isEmpty()) {
                throw new FileNotFoundException("No files found for the provided code.");
            }

            // create a zip file in the base directory.
            try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                // add each file to the zip.
                for (String fileUrl : fileUrls) {
                    File fileToZip = new File(fileUrl);
                    try (FileInputStream fis = new FileInputStream(fileToZip)) {
                        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                        zipOut.putNextEntry(zipEntry);

                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipOut.write(bytes, 0, length);
                        }
                    }
                }
            }

            log.info("Zip file created for code {}", uniqueCode);
            return zipFilePath;

        } catch (IOException e) {
            log.error("Error while creating zip file for code {}", uniqueCode, e);
            throw new FileUploadFailedException("Failed to create zip file!");
        }
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
