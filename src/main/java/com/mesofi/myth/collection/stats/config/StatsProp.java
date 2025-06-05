package com.mesofi.myth.collection.stats.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stats")
public record StatsProp(int minMatchingDistance, List<String> ignorableKeywords) {}
