package com.ghostdrop.config.gauges;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UploadActivityGauge {

    private final AtomicInteger activeUploads = new AtomicInteger(0);

    public UploadActivityGauge(MeterRegistry registry) {
        Gauge.builder("ghostdrop.uploads.active", activeUploads, AtomicInteger::get)
                .description("Currently active uploads")
                .register(registry);
    }

    public void increment() {
        activeUploads.incrementAndGet();
    }

    public void decrement() {
        activeUploads.decrementAndGet();
    }
}

