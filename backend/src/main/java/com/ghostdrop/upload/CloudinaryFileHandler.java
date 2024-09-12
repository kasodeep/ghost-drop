package com.ghostdrop.upload;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ghostdrop.entity.UrlMapping;
import com.ghostdrop.services.UrlMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class CloudinaryFileHandler implements FileHandler {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UrlMappingService mappingService;

    @Override
    public String upload(MultipartFile[] multipartFiles, String folderName) {
        Map<Object, Object> utils = new HashMap<>();
        utils.put("folder", folderName);
        utils.put("use_filename", true);
        utils.put("unique_filename", false);

        List<String> fileUrls = new ArrayList<>();

        try {
            for (MultipartFile file : multipartFiles) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null && originalFilename.contains(".")) {
                    originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
                }

                // Set the public_id to the filename without the extension
                utils.put("public_id", originalFilename);

                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), utils);

                System.out.println(uploadResult.get("public_id"));
                String url = (String) uploadResult.get("secure_url");
                fileUrls.add(url);
            }

            String uniqueCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

            UrlMapping urlMapping = mappingService.save(uniqueCode, fileUrls);
            log.info("{} save url", urlMapping.toString());

            return uniqueCode;
        } catch (IOException e) {
            for (String secureUrl : fileUrls) {
                delete(secureUrl);
            }
            throw new RuntimeException("Image Uploading Failed!!");
        }
    }

    public boolean delete(String secureUrl) {
        try {
            String publicId = extractPublicIdFromUrl(secureUrl);
            Map<Object, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            if (!"ok".equals(result.get("result"))) {
                throw new RuntimeException("Failed to delete file from Cloudinary: " + secureUrl);
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file: " + e);
        }
    }

    @Override
    public UrlMapping getFiles(String uniqueCode) {
        return mappingService.get(uniqueCode);
    }


    private String extractPublicIdFromUrl(String secureUrl) {
        // "https://res.cloudinary.com/deepkcloud/image/upload/v1726127710/anonymous/Resume.pdf.pdf"
        String[] parts = secureUrl.split("/");

        // Ensure the URL contains enough parts for a valid Cloudinary URL
        if (parts.length < 2) {
            throw new RuntimeException("Invalid Cloudinary URL: " + secureUrl);
        }

        // The public ID is in the last part of the URL (before the file extension)
        String publicIdWithExtension = parts[parts.length - 1];
        String folderName = parts[parts.length - 2];

        // Remove the file extension from the public ID
        int lastDotIndex = publicIdWithExtension.lastIndexOf('.');
        publicIdWithExtension = (lastDotIndex == -1) ? publicIdWithExtension : publicIdWithExtension.substring(0, lastDotIndex);
        return folderName + "/" + publicIdWithExtension;
    }
}
