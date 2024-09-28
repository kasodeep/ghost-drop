package com.ghostdrop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Backend: Ghost Drop
 */
@SpringBootApplication
@EnableScheduling
public class GhostDropApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhostDropApplication.class, args);
    }

}
