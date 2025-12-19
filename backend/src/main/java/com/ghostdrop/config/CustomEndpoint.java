package com.ghostdrop.config;

import lombok.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * The class is a custom endpoint for exposing some metrics.
 * Not related to health, info and internal boot.
 */
@Component
@Endpoint(id = "custom-point")
@Value
public class CustomEndpoint {

    /**
     * The read method returns a basic string for endpoint check.
     */
    @ReadOperation
    public String read() {
        return "custom-point";
    }
}
