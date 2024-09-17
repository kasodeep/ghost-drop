package com.ghostdrop.strategy.delete;

import java.util.List;

/**
 * Upload strategy defines the interface to concrete and different deleting strategies.
 *
 * @author Kasodariya Deep
 */
public interface DeleteStrategy {

    /**
     * deleteFiles takes the list of file urls and deletes them from the server.
     *
     * @param fileUrls The paths of files to be swept.
     */
    void deleteFiles(List<String> fileUrls);
}

