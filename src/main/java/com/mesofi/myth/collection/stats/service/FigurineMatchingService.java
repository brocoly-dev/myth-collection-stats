package com.mesofi.myth.collection.stats.service;

import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.LineUp;
import com.mesofi.myth.collection.stats.config.StatsProp;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
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
   * Finds the best matching figurine from a list of figurines based on the target name.
   *
   * @param figurines the list of figurines to search through for the best match
   * @param targetName the name of the figurine to find a match for
   * @return an Optional containing the best matching figurine if found within the configured
   *     distance threshold, or empty if no suitable match is found
   */
  public Optional<Figurine> findFigurineBestMatch(
      @Nonnull List<Figurine> figurines, @NotEmpty String targetName) {

    // Preprocess the target name by removing ignorable keywords
    targetName = removeContainedWords(targetName, statsProp.ignorableKeywords());

    // Determine the LineUp based on the target name
    LineUp lineUpFound = findLineUpBasedOnName(targetName);

    // If the target name contains "Original Color Edition" or "Original Color", remove it
    boolean isOce = containsAnyWord(targetName, "Original");
    if (isOce) {
      targetName = removeContainedWords(targetName, List.of("Original", "Color"));
    }

    LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
    int dist;
    int minDistance = Integer.MAX_VALUE;
    Figurine bestMatchFigurine = null;
    for (Figurine f :
        figurines.stream()
            .filter(Objects::nonNull)
            .filter(f -> f.getLineUp() == lineUpFound)
            .filter(f -> f.isOce() == isOce)
            .toList()) {
      dist = levenshtein.apply(targetName.toLowerCase(), f.getBaseName().toLowerCase());
      if (dist < minDistance) {
        minDistance = dist;
        bestMatchFigurine = f;
      }
    }

    Optional<Figurine> bestOptionalMatchFigurine = Optional.ofNullable(bestMatchFigurine);
    // If the best match is found, check if the distance is within the configured threshold
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
   * Determines the appropriate LineUp enum value based on the target name by searching for matching
   * keywords in the LineUp descriptions.
   *
   * @param targetName the name to analyze for LineUp classification.
   * @return the matching LineUp if found, otherwise defaults to LineUp.MYTH_CLOTH.
   */
  private LineUp findLineUpBasedOnName(String targetName) {
    LineUp lineUp = LineUp.MYTH_CLOTH;

    for (LineUp currLineUp : LineUp.values()) {
      if (containsAnyWord(targetName, currLineUp.getDescription())) {
        lineUp = currLineUp;
        break;
      }
    }

    return lineUp;
  }

  /**
   * Checks if any word from the first string is contained as a complete word in the second string.
   * The comparison is case-insensitive and uses word boundaries to ensure exact word matching.
   *
   * @param string1 the source string containing words to search for, separated by whitespace.
   * @param string2 the target string to search within.
   * @return true if any complete word from string1 is found in string2, false otherwise.
   */
  private boolean containsAnyWord(String string1, String string2) {
    String[] words = string1.split("\\s+");
    String lowerString2 = string2.toLowerCase();

    for (String word : words) {
      String cleanWord = word.toLowerCase().trim();
      if (!cleanWord.isEmpty()) {
        // Use word boundaries to match exact words
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(cleanWord) + "\\b");
        if (pattern.matcher(lowerString2).find()) {
          return true;
        }
      }
    }
    return false;
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
