package com.mesofi.myth.collection.stats.service;

import com.mesofi.myth.collection.stats.config.StatsProp;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

/**
 * Service for matching figurine names using string similarity algorithms. Provides functionality to
 * find the best matching figurine name from a collection based on a target name using Levenshtein
 * distance calculation.
 */
@Log4j2
@Service
public class FigurineMatchingService {

  private final StatsProp statsProp;

  public FigurineMatchingService(StatsProp statsProp) {
    this.statsProp = statsProp;
  }

  /**
   * Finds the best matching figurine name from a collection based on a target name using
   *
   * @param figurineNames the list of figurine names to search from.
   * @param targetName the target name to match against the figurine names.
   * @return the best matching figurine name, or an empty Optional if no match is found.
   */
  public Optional<String> findBestMatch(
      @Nonnull List<String> figurineNames, @NotEmpty String targetName) {

    targetName = removeContainedWords(targetName, statsProp.ignorableKeywords());

    LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

    String bestMatch = null;
    int minDistance = Integer.MAX_VALUE;

    for (String figurineName : figurineNames) {
      if (Objects.nonNull(figurineName)) {
        int distance =
            levenshteinDistance.apply(targetName.toLowerCase(), figurineName.toLowerCase());
        if (distance < minDistance) {
          minDistance = distance;
          bestMatch = figurineName;
        }
      }
    }

    if (minDistance > statsProp.minMatchingDistance()) {
      log.warn(
          "No figurine found for: '{}', minDistance: {}, expectedMinDistance: {}",
          targetName,
          minDistance,
          statsProp.minMatchingDistance());
      return Optional.empty();
    } else {
      log.info(
          "The best match for figurine: '{}' is: '{}', minDistance: {}, expectedMinDistance: {}",
          targetName,
          bestMatch,
          minDistance,
          statsProp.minMatchingDistance());
      return Optional.ofNullable(bestMatch);
    }
  }

  private String removeContainedWords(String targetName, List<String> keywordsToRemove) {
    String result = targetName;
    for (String keyword : keywordsToRemove) {
      if (result.toLowerCase().contains(keyword.toLowerCase())) {
        int startIndex = result.toLowerCase().indexOf(keyword.toLowerCase());
        int endIndex = startIndex + keyword.length();
        result = result.substring(0, startIndex) + result.substring(endIndex);
      }
    }
    log.info(
        "After removing keywords, the figurine: '{}' was converted to: '{}'",
        targetName,
        result.trim());
    return result.trim();
  }
}
