package com.ghostdrop.upload;

import com.ghostdrop.entity.UrlMapping;
import org.springframework.web.multipart.MultipartFile;

public interface FileHandler {

    String upload(MultipartFile[] multipartFiles, String folderName);

    boolean delete(String secureUrl);

    UrlMapping getFiles(String uniqueCode);
}
