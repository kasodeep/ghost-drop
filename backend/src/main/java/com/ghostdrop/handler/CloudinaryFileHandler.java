package com.ghostdrop.handler;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ghostdrop.exceptions.FileDeleteFailedException;
import com.ghostdrop.exceptions.FileUploadFailedException;
import com.ghostdrop.exceptions.InValidUrlException;
import com.ghostdrop.responses.CodeResponse;
import com.ghostdrop.services.UrlMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * CloudinaryFileHandler is the magic place where the actual logic is implemented.
 *
 * @author Kasodariya Deep
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "file.handler", havingValue = "cloudinary")
public class CloudinaryFileHandler implements FileHandler {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UrlMappingService mappingService;

    @Override
    public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
        // utils needed while uploading the files.
        Map<Object, Object> utils = new HashMap<>();
        utils.put("folder", folderName);
        utils.put("use_filename", true);
        utils.put("unique_filename", false);

        // list to store the uploaded files urls.
        List<String> fileUrls = new ArrayList<>();
        String uniqueCode = generateUniqueCode();

        try {
            for (MultipartFile file : multipartFiles) {
                String fileNameWithExtension = file.getOriginalFilename();
                String fileNameWithoutExtension = getFileNameWithoutExtension(fileNameWithExtension);
                utils.put("public_id", fileNameWithoutExtension + uniqueCode);

                Map<String, Object> uploadResult = cloudinary
                        .uploader()
                        .upload(file.getBytes(), utils);

                // getting the secured url to access the file.
                String url = (String) uploadResult.get("secure_url");
                log.info("File uploaded with name {}", fileNameWithExtension);
                fileUrls.add(url);
            }

            mappingService.save(uniqueCode, fileUrls);
            return new CodeResponse(uniqueCode);

        } catch (IOException e) {
            log.error("Deleting due to error in uploading!");
            delete(uniqueCode, fileUrls);
            throw new FileUploadFailedException("Failed to upload the images!");
        }
    }

    @Override
    public void delete(String uniqueCode, List<String> fileUrls) {
        try {
            for (String secureUrl : fileUrls) {
                String publicId = extractPublicIdFromUrl(secureUrl);

                Map<Object, Object> result = cloudinary
                        .uploader()
                        .destroy(publicId, ObjectUtils.emptyMap());

                if (!"ok".equals(result.get("result"))) {
                    throw new FileDeleteFailedException("Failed to delete file from Cloudinary: " + secureUrl);
                }
                log.info("File with url: {} deleted", secureUrl);
            }

        } catch (Exception e) {
            throw new FileDeleteFailedException("Failed to delete files from Cloudinary: " + e);
        }
    }

    @Override
    public Path getFiles(String uniqueCode) {
        return null;
    }

    private String extractPublicIdFromUrl(String secureUrl) {
        String[] parts = secureUrl.split("/");
        if (parts.length < 2) {
            throw new InValidUrlException("Invalid Cloudinary URL: " + secureUrl);
        }

        String publicID = getPublicID(parts);
        return publicID;
    }

    private String getPublicID(String[] parts) {
        String publicIdWithExtension = parts[parts.length - 1];
        String folderName = parts[parts.length - 2];

        int lastDotIndex = publicIdWithExtension.lastIndexOf('.');
        String publicIdWithOutExtension = (lastDotIndex == -1) ? publicIdWithExtension : publicIdWithExtension.substring(0, lastDotIndex);

        return folderName + "/" + publicIdWithOutExtension;
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String getFileNameWithoutExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            originalFilename = originalFilename
                    .substring(0, originalFilename.lastIndexOf('.'));
        }
        return originalFilename;
    }
}
