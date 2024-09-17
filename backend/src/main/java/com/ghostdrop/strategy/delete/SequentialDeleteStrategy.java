package com.ghostdrop.strategy.delete;

import com.ghostdrop.exceptions.FileDeleteFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * SequentialDeleteStrategy deletes the files one by one.
 *
 * @author Kasodariya Deep
 */
@Slf4j
@Component
public class SequentialDeleteStrategy implements DeleteStrategy {

    @Override
    public void deleteFiles(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            try {
                Path path = Paths.get(fileUrl);
                Files.deleteIfExists(path);

                log.info("File with path {} deleted", fileUrl);
            } catch (IOException e) {
                log.error("{} error occurred", e.getClass());
                log.error("message: {}", e.getMessage());

                throw new FileDeleteFailedException("Failed to delete the file!");
            }
        }
    }
}

