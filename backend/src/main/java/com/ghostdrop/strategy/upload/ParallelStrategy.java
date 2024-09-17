package com.ghostdrop.strategy.upload;

import com.ghostdrop.exceptions.FileUploadFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * ParallelStrategy stores the files using CompletableFuture to achieve a faster parallel response.
 *
 * @author Kasodariya Deep
 */
@Slf4j
@Component
public class ParallelStrategy implements UploadStrategy {

    @Override
    public List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode) {
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (MultipartFile file : files) {

            // run each task on different thread.
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    String fileNameWithExtension = file.getOriginalFilename();
                    if (fileNameWithExtension == null) {
                        throw new FileNotFoundException("One of the files has no name!!");
                    }

                    Path filePath = folderPath.resolve(fileNameWithExtension);
                    Files.write(filePath, file.getBytes());
                    return filePath.toString();

                } catch (IOException e) {
                    log.error("{} error occurred", e.getClass());
                    log.error("message: {}", e.getMessage());

                    throw new FileUploadFailedException("Failed to upload the files!");
                }
            });
            futures.add(future);
        }

        // wait for all uploads to complete.
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        return futures
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
