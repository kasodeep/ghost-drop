package com.ghostdrop.strategy.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The class provides an interface to select one of the delete strategies based on environment & no of files.
 */
@Service
public class DeleteStrategySelector {

    @Value("${parallel.threshold}")
    private Integer parallelStrategyThreshold;

    @Autowired
    private SequentialDeleteStrategy sequentialDeleteStrategy;

    @Autowired
    private ParallelDeleteStrategy parallelDeleteStrategy;

    public DeleteStrategy selectStrategy(int numberOfFiles) {
        if (numberOfFiles > parallelStrategyThreshold) {
            return parallelDeleteStrategy;
        } else {
            return sequentialDeleteStrategy;
        }
    }
}

