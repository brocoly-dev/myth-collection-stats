package com.mesofi.myth.collection.stats;

import com.mesofi.myth.collection.stats.config.StatsProp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Main Spring Boot application class for the Myth Collection Stats service. This class serves as
 * the entry point for the application and configures Spring Boot autoconfiguration.
 */
@SpringBootApplication
@EnableRetry
@EnableConfigurationProperties(StatsProp.class)
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
