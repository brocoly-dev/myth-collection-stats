package com.mesofi.myth.collection.stats.service;

import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.stats.config.StatsProp;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class FigurineMatchingService {

  /** The statistics properties configuration used for figurine matching operations. */
  private final StatsProp statsProp;

  /**
   * Constructs a new FigurineMatchingService with the specified statistics properties.
   *
   * @param statsProp the statistics properties configuration containing matching parameters such as
   *     ignorable keywords and minimum matching distance threshold
   */
  public FigurineMatchingService(StatsProp statsProp) {
    this.statsProp = statsProp;
  }

  /**
   * Finds the best matching figurine from a collection based on a target name using Levenshtein
   * distance. The method first removes ignorable keywords from the target name, then calculates the
   * string similarity between the processed target name and each figurine's displayable name.
   * Returns the figurine with the smallest distance if it meets the minimum matching distance
   * threshold.
   *
   * @param figurines the list of figurines to search through for matches. Null entries are filtered
   *     out.
   * @param targetName the name to match against figurine names. Must not be empty.
   * @return an Optional containing the best matching figurine if found and within distance
   *     threshold, or empty Optional if no suitable match is found.
   */
  public Optional<Figurine> findFigurineBestMatch(
      @Nonnull List<Figurine> figurines, @NotEmpty String targetName) {

    targetName = removeContainedWords(targetName, statsProp.ignorableKeywords());

    LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();

    int dist;
    int minDistance = Integer.MAX_VALUE;
    Figurine bestMatchFigurine = null;
    for (Figurine f : figurines.stream().filter(Objects::nonNull).toList()) {
      dist = levenshtein.apply(targetName.toLowerCase(), f.getDisplayableName().toLowerCase());
      if (dist < minDistance) {
        minDistance = dist;
        bestMatchFigurine = f;
      }
    }

    Optional<Figurine> bestOptionalMatchFigurine = Optional.ofNullable(bestMatchFigurine);
    if (bestOptionalMatchFigurine.isPresent()) {
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
            bestOptionalMatchFigurine.get().getDisplayableName(),
            minDistance,
            statsProp.minMatchingDistance());
        return bestOptionalMatchFigurine;
      }
    } else {
      log.warn("Unable to find figurine for: '{}' in invalid dataset", targetName);
      return Optional.empty();
    }
  }

  /**
   * Removes specified keywords from the target name by finding and eliminating their occurrences.
   * The comparison is case-insensitive, but the original casing of the remaining text is preserved.
   *
   * @param targetName the original string from which keywords should be removed.
   * @param keywordsToRemove the list of keywords to search for and remove from the target name.
   * @return the modified string with keywords removed and trimmed of leading/trailing whitespace.
   */
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
