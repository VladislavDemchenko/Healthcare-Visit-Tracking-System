package org.demchenko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HealthcareVisitTrackerApp {
    public static void main(String[] args) {
        SpringApplication.run(HealthcareVisitTrackerApp.class, args);
    }
}