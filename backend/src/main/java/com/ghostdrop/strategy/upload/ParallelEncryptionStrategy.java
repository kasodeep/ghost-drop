package com.ghostdrop.strategy.upload;

import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.utils.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The class stores the files by encrypting it and storing it parallel using java multithreading.
 *
 * @author Kasodariya Deep
 */
@Slf4j
@Component
public class ParallelEncryptionStrategy implements UploadStrategy {

    @Override
    public List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode) {
        byte[] secretKey;

        try {
            secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);
        } catch (Exception e) {
            throw new FileUploadFailedException("Failed to upload the files!");
        }

        List<CompletableFuture<String>> futures = Arrays
                .stream(files)
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        byte[] fileBytes = file.getBytes();
                        byte[] encryptedBytes = EncryptionUtil.encrypt(fileBytes, secretKey);

                        String fileNameWithExtension = file.getOriginalFilename();
                        if (fileNameWithExtension == null) {
                            throw new FileNotFoundException("One of the files has no name!!");
                        }

                        Path filePath = folderPath.resolve(fileNameWithExtension);
                        Files.write(filePath, encryptedBytes);
                        return filePath.toString();

                    } catch (Exception e) {
                        log.error("{} error occurred", e.getClass());
                        log.error("message: {}", e.getMessage());

                        throw new FileUploadFailedException("Failed to upload the files!");
                    }
                }))
                .toList();

        return futures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
