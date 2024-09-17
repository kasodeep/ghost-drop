package com.ghostdrop.handler;

import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.exceptions.FileDeleteFailedException;
import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.exceptions.GeneralError;
import com.ghostdrop.exceptions.TimeExpiredException;
import com.ghostdrop.responses.CodeResponse;
import com.ghostdrop.services.UrlMappingService;
import com.ghostdrop.strategy.delete.DeleteStrategy;
import com.ghostdrop.strategy.delete.DeleteStrategySelector;
import com.ghostdrop.strategy.upload.UploadStrategy;
import com.ghostdrop.strategy.upload.UploadStrategySelector;
import com.ghostdrop.utils.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
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
    private UploadStrategySelector uploadStrategySelector;

    @Autowired
    private DeleteStrategySelector deleteStrategySelector;

    @Autowired
    private UrlMappingService mappingService;

    @Override
    public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
        List<String> fileUrls;

        String uniqueCode = generateUniqueCode();
        Path folderPath = Paths.get(BASE_DIRECTORY, folderName, uniqueCode);

        try {
            // create directories for the new files.
            Files.createDirectories(folderPath);

            UploadStrategy strategy = uploadStrategySelector.selectStrategy(multipartFiles.length);
            fileUrls = strategy.uploadFiles(multipartFiles, folderPath, uniqueCode);

            // save file URLs with the unique code.
            mappingService.save(uniqueCode, fileUrls);
            return new CodeResponse(uniqueCode);

        } catch (Exception e) {
            throw new FileUploadFailedException("Failed to upload the files!");
        }
    }

    @Override
    public void delete(String uniqueCode, List<String> fileUrls) {
        try {
            DeleteStrategy deleteStrategy = deleteStrategySelector.selectStrategy(fileUrls.size());
            deleteStrategy.deleteFiles(fileUrls);

            // delete the code directory.
            Path codePath = Paths.get(BASE_DIRECTORY, "anonymous", uniqueCode);
            Files.deleteIfExists(codePath);

            // delete the zip file.
            Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");
            Files.deleteIfExists(zipFilePath);
        } catch (Exception e) {
            throw new FileDeleteFailedException("Failed to delete files for code: " + uniqueCode);
        }
    }

    @Override
    public Path getFiles(String uniqueCode) {
        try {
            // create the path for the zip file.
            Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");

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

            byte[] secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

            // create a zip file in the base directory.
            try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                // add each file to the zip.
                for (String fileUrl : fileUrls) {
                    Path filePath = Paths.get(fileUrl);
                    byte[] encryptedFileBytes = Files.readAllBytes(filePath);  // Read encrypted file as bytes
                    byte[] decryptedBytes = EncryptionUtil.decrypt(encryptedFileBytes, secretKey);  // Decrypt the file bytes

                    // Add the decrypted file to the zip
                    ZipEntry zipEntry = new ZipEntry(filePath.getFileName().toString());
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(decryptedBytes);
                    zipOut.closeEntry();
                }
            }

            log.info("Zip file created for code {}", uniqueCode);
            return zipFilePath;

        } catch (EntityNotFoundException | TimeExpiredException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralError("Looks like server fault!!");
        }
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
