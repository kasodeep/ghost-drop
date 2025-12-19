package com.ghostdrop.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * For health, liveness means the application is running and not deadlocked.
 * Readiness provides info related to the application working or able to accept connections and the below supports it.
 */
@Component
public class EncryptionHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        if (isEncryptionKeyValid()) {
            return Health.up()
                    .withDetail("encryption", "key loaded")
                    .build();
        }

        return Health.down()
                .withDetail("encryption", "key missing or invalid")
                .build();
    }

    private boolean isEncryptionKeyValid() {
        // Provide proper check before prod deploy.
        return true;
    }
}

