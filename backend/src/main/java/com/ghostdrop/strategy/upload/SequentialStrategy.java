package com.ghostdrop.strategy.upload;

import com.ghostdrop.exceptions.FileUploadFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * SequentialStrategy stores the files one by one without encryption or parallelism.
 *
 * @author Kasodariya Deep
 */
@Slf4j
@Component
public class SequentialStrategy implements UploadStrategy {

    @Override
    public List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileNameWithExtension = file.getOriginalFilename();
                if (fileNameWithExtension == null) {
                    throw new FileNotFoundException("One of the files has no name!!");
                }

                Path filePath = folderPath.resolve(fileNameWithExtension);
                Files.write(filePath, file.getBytes());
                fileUrls.add(filePath.toString());

            } catch (IOException e) {
                log.error("{} error occurred", e.getClass());
                log.error("message: {}", e.getMessage());

                throw new FileUploadFailedException("Failed to upload the files!");
            }
        }
        return fileUrls;
    }
}
