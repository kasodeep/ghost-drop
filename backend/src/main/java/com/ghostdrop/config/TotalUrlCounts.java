package com.ghostdrop.config;

import com.ghostdrop.repository.UrlMappingRepository;
import lombok.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The class provides programmatic way to add information to the `info` endpoint.
 */
@Component
@Value
public class TotalUrlCounts implements InfoContributor {

    UrlMappingRepository urlMappingRepository;

    /**
     * The contribute method uses the Info builder to add data.
     */
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app-urls.stats", Map.of("count", urlMappingRepository.count()));
    }
}
