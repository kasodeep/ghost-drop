package com.ghostdrop.upload;

import com.ghostdrop.responses.CodeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

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
     * delete removes the files using the urls and the code directories.
     */
    void delete(String uniqueCode, List<String> fileUrls);

    /**
     * getFiles creates a zip of the files associated with code and returns the path.
     */
    Path getFiles(String uniqueCode);
}