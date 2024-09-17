package com.ghostdrop.strategy.delete;

import com.ghostdrop.exceptions.FileDeleteFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SequentialDeleteStrategy deletes the files using concurrency.
 *
 * @author Kasodariya Deep
 */
@Slf4j
@Component
public class ParallelDeleteStrategy implements DeleteStrategy {

    @Override
    public void deleteFiles(List<String> fileUrls) {
        List<CompletableFuture<Void>> futures = fileUrls
                .stream()
                .map(fileUrl -> CompletableFuture.runAsync(() -> {
                            try {
                                Path path = Paths.get(fileUrl);
                                Files.deleteIfExists(path);
                                log.info("File with path {} deleted", fileUrl);

                            } catch (IOException e) {
                                log.error("{} error occurred", e.getClass());
                                log.error("message: {}", e.getMessage());

                                throw new FileDeleteFailedException("Failed to delete the file!");
                            }
                        })
                ).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}

