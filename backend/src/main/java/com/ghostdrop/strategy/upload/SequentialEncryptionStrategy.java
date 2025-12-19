package com.ghostdrop.strategy.upload;

import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.utils.EncryptionUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SequentialEncryptionStrategy implements UploadStrategy {

    private final Timer encryptionTimer;

    public SequentialEncryptionStrategy(MeterRegistry meterRegistry) {
        this.encryptionTimer =
                meterRegistry.timer("ghostdrop.encrypt.duration");
    }

    @Override
    public List<String> uploadFiles(
            MultipartFile[] files,
            Path folderPath,
            String uniqueCode
    ) {

        List<String> fileUrls = new ArrayList<>();

        try {
            byte[] secretKey =
                    EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

            for (MultipartFile file : files) {
                encryptionTimer.record(() -> {
                    try {
                        if (file.getOriginalFilename() == null) {
                            throw new FileNotFoundException("File name missing");
                        }

                        byte[] encryptedBytes =
                                EncryptionUtil.encrypt(file.getBytes(), secretKey);

                        Path filePath =
                                folderPath.resolve(file.getOriginalFilename());

                        Files.write(filePath, encryptedBytes);
                        fileUrls.add(filePath.toString());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            return fileUrls;

        } catch (Exception e) {
            log.error("Encryption upload failed", e);
            throw new FileUploadFailedException("Failed to upload the files!");
        }
    }
}
