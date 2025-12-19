package com.ghostdrop.strategy.upload;

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

    private final SequentialStrategy sequentialStrategy;

    private final ParallelStrategy parallelStrategy;

    private final ParallelEncryptionStrategy parallelEncryptionStrategy;

    private final SequentialEncryptionStrategy sequentialEncryptionStrategy;

    public UploadStrategySelector(SequentialStrategy sequentialStrategy, ParallelStrategy parallelStrategy, ParallelEncryptionStrategy parallelEncryptionStrategy, SequentialEncryptionStrategy sequentialEncryptionStrategy) {
        this.sequentialStrategy = sequentialStrategy;
        this.parallelStrategy = parallelStrategy;
        this.parallelEncryptionStrategy = parallelEncryptionStrategy;
        this.sequentialEncryptionStrategy = sequentialEncryptionStrategy;
    }

    public UploadStrategy selectStrategy(int numberOfFiles) {
        if (numberOfFiles > parallelStrategyThreshold) {
            return encryptionEnabled ? parallelEncryptionStrategy : parallelStrategy;
        } else {
            return encryptionEnabled ? sequentialEncryptionStrategy : sequentialStrategy;
        }
    }
}