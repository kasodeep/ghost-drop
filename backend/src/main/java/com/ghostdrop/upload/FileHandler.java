package com.ghostdrop.upload;

import com.ghostdrop.responses.CodeResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileHandler handles the uploading, deleting and the retrieval of files from the cloud.
 *
 * @author Kasodariya Deep
 */
public interface FileHandler {

    /**
     * upload uploads the files to the corresponding folder.
     */
    CodeResponse upload(MultipartFile[] multipartFiles, String folderName);

    /**
     * delete removes the files from the cloud using the url.
     */
    boolean delete(String url);
}
