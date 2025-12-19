package com.ghostdrop.handler;

import com.ghostdrop.config.gauges.UploadActivityGauge;
import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.exceptions.FileDeleteFailedException;
import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.exceptions.TimeExpiredException;
import com.ghostdrop.responses.CodeResponse;
import com.ghostdrop.services.UrlMappingService;
import com.ghostdrop.strategy.delete.DeleteStrategy;
import com.ghostdrop.strategy.delete.DeleteStrategySelector;
import com.ghostdrop.strategy.upload.UploadStrategy;
import com.ghostdrop.strategy.upload.UploadStrategySelector;
import com.ghostdrop.utils.EncryptionUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@ConditionalOnProperty(name = "file.handler", havingValue = "server")
public class ServerFileHandler implements FileHandler {

    private final MeterRegistry meterRegistry;
    private final UploadStrategySelector uploadStrategySelector;
    private final DeleteStrategySelector deleteStrategySelector;
    private final UrlMappingService mappingService;

    @Value("${base.directory}")
    private String BASE_DIRECTORY;

    /* ----------- Metrics ----------- */
    private final Timer uploadTimer;
    private final Timer zipTimer;
    private final Counter uploadedFilesCounter;
    private final Counter uploadFailureCounter;
    private final DistributionSummary zipSizeSummary;
    private final UploadActivityGauge uploadActivityGauge;

    public ServerFileHandler(
            MeterRegistry meterRegistry,
            UploadStrategySelector uploadStrategySelector,
            DeleteStrategySelector deleteStrategySelector,
            UrlMappingService mappingService,
            UploadActivityGauge uploadActivityGauge
    ) {
        this.meterRegistry = meterRegistry;
        this.uploadStrategySelector = uploadStrategySelector;
        this.deleteStrategySelector = deleteStrategySelector;
        this.mappingService = mappingService;
        this.uploadActivityGauge = uploadActivityGauge;

        this.uploadTimer = meterRegistry.timer("ghostdrop.upload.duration");
        this.zipTimer = meterRegistry.timer("ghostdrop.zip.duration");

        this.uploadedFilesCounter =
                meterRegistry.counter("ghostdrop.files.uploaded");

        this.uploadFailureCounter =
                meterRegistry.counter("ghostdrop.upload.failed");

        this.zipSizeSummary =
                meterRegistry.summary("ghostdrop.zip.size.bytes");
    }

    @Override
    public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {

        String uniqueCode = generateUniqueCode();
        Path folderPath = Paths.get(BASE_DIRECTORY, folderName, uniqueCode);

        uploadActivityGauge.increment();
        try {
            Files.createDirectories(folderPath);

            UploadStrategy strategy = uploadStrategySelector.selectStrategy(multipartFiles.length);

            List<String> fileUrls = uploadTimer.record(
                    () -> strategy.uploadFiles(multipartFiles, folderPath, uniqueCode)
            );

            uploadedFilesCounter.increment(multipartFiles.length);

            mappingService.save(uniqueCode, fileUrls);
            return new CodeResponse(uniqueCode);

        } catch (Exception e) {
            uploadFailureCounter.increment();
            throw new FileUploadFailedException("Failed to upload the files!");
        } finally {
            uploadActivityGauge.decrement();
        }
    }

    @Override
    public void delete(String uniqueCode, List<String> fileUrls) {
        try {
            DeleteStrategy deleteStrategy =
                    deleteStrategySelector.selectStrategy(fileUrls.size());

            deleteStrategy.deleteFiles(fileUrls);

            Files.deleteIfExists(
                    Paths.get(BASE_DIRECTORY, "anonymous", uniqueCode)
            );

            Files.deleteIfExists(
                    Paths.get(BASE_DIRECTORY, uniqueCode + ".zip")
            );

        } catch (Exception e) {
            throw new FileDeleteFailedException(
                    "Failed to delete files for code: " + uniqueCode
            );
        }
    }

    @Override
    public Path getFiles(String uniqueCode) {
        try {
            Path zipFilePath = Paths.get(BASE_DIRECTORY, uniqueCode + ".zip");

            if (Files.exists(zipFilePath)) {
                return zipFilePath;
            }

            UrlMapping urlMapping = mappingService.get(uniqueCode);
            List<String> fileUrls = urlMapping.getUrls();

            if (fileUrls == null || fileUrls.isEmpty()) {
                throw new FileNotFoundException("No files found.");
            }

            byte[] secretKey =
                    EncryptionUtil.generateKeyFromUniqueCode(uniqueCode);

            zipTimer.record(() -> {
                try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                     ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                    for (String fileUrl : fileUrls) {
                        Path filePath = Paths.get(fileUrl);
                        byte[] encryptedBytes = Files.readAllBytes(filePath);
                        byte[] decryptedBytes =
                                EncryptionUtil.decrypt(encryptedBytes, secretKey);

                        ZipEntry zipEntry =
                                new ZipEntry(filePath.getFileName().toString());

                        zipOut.putNextEntry(zipEntry);
                        zipOut.write(decryptedBytes);
                        zipOut.closeEntry();
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            zipSizeSummary.record(Files.size(zipFilePath));
            return zipFilePath;

        } catch (EntityNotFoundException | TimeExpiredException e) {
            throw e;
        } catch (Exception e) {
            throw new EntityNotFoundException("No files found for the code!");
        }
    }

    private String generateUniqueCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }
}
