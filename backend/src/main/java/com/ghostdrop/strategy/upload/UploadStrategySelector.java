package com.ghostdrop.strategy.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The class provides an interface to select one of the upload strategies based on environment & no of files.
 */
@Service
public class UploadStrategySelector {

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${parallel.threshold}")
    private Integer parallelStrategyThreshold;

    @Autowired
    private SequentialStrategy sequentialStrategy;

    @Autowired
    private ParallelStrategy parallelStrategy;

    @Autowired
    private ParallelEncryptionStrategy parallelEncryptionStrategy;

    @Autowired
    private SequentialEncryptionStrategy sequentialEncryptionStrategy;

    public UploadStrategy selectStrategy(int numberOfFiles) {
        if (numberOfFiles > parallelStrategyThreshold) {
            return encryptionEnabled ? parallelEncryptionStrategy : parallelStrategy;
        } else {
            return encryptionEnabled ? sequentialEncryptionStrategy : sequentialStrategy;
        }
    }
}