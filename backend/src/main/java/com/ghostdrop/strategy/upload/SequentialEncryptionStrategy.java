package com.ghostdrop.strategy.upload;

import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.utils.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The class stores the files by encrypting it and storing it sequentially one after others.
 *
 * @author Kasodariya Deep
 */
@Slf4j
@Component
public class SequentialEncryptionStrategy implements UploadStrategy {

    @Override
    public List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode) {
        List<String> fileUrls = new ArrayList<>();

        try {
            byte[] secretKey = EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

            for (MultipartFile file : files) {
                byte[] fileBytes = file.getBytes();
                byte[] encryptedBytes = EncryptionUtil.encrypt(fileBytes, secretKey);

                String fileNameWithExtension = file.getOriginalFilename();
                if (fileNameWithExtension == null) {
                    throw new FileNotFoundException("One of the files has no name!!");
                }

                Path filePath = folderPath.resolve(fileNameWithExtension);
                Files.write(filePath, encryptedBytes);
                fileUrls.add(filePath.toString());
            }
        } catch (Exception e) {
            log.error("{} error occurred", e.getClass());
            log.error("message: {}", e.getMessage());

            throw new FileUploadFailedException("Failed to upload the files!");
        }
        return fileUrls;
    }
}
