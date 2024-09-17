package com.ghostdrop.strategy.upload;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

/**
 * Upload strategy defines the interface to concrete and different upload strategies.
 *
 * @author Kasodariya Deep
 */
public interface UploadStrategy {

    /**
     * uploadFiles takes the files and folderPath along with the unique code and stores the files on the server.
     *
     * @param files      The files to be uploaded.
     * @param uniqueCode The uniqueCode associated with all the files.
     * @param folderPath The path where the files are to be stored.
     */
    List<String> uploadFiles(MultipartFile[] files, Path folderPath, String uniqueCode);
}
