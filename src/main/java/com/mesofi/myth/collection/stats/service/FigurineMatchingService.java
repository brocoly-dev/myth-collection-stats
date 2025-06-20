package com.mesofi.myth.collection.stats.service;

import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.LineUp;
import com.mesofi.myth.collection.stats.config.StatsProp;
import com.mesofi.myth.collection.stats.model.NameFinder;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Slf4j
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
    targetName = removeUnwantedWords(targetName, statsProp.ignorableKeywords());

    // Determine the LineUp based on the target name
    NameFinder nameFinder = findLineUpUsing(targetName);
    LineUp lineUpFound = nameFinder.lineUp();
    targetName = nameFinder.targetName();

    // Determine if the figurine is an Original Collection Edition (OCE)
    nameFinder = findOceUsing(targetName);
    boolean isOce = nameFinder.oce();
    targetName = nameFinder.targetName();

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
            bestOptionalMatchFigurine.get().getBaseName(),
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
   * Determines the lineup category for a figurine based on keywords in the target name. Searches
   * through all available LineUp enum values to find a match with the target name. If no specific
   * lineup is found, defaults to MYTH_CLOTH. Removes the identified lineup description from the
   * target name to clean it for further processing.
   *
   * @param targetName the original string to analyze for lineup indicators.
   * @return a NameFinder object with the cleaned target name, identified lineup, and OCE flag set
   *     to false.
   */
  private NameFinder findLineUpUsing(String targetName) {
    LineUp lineUp = LineUp.MYTH_CLOTH;

    for (LineUp currLineUp : LineUp.values()) {
      if (containsAnyWord(targetName, currLineUp.getDescription())) {
        lineUp = currLineUp;
        break;
      }
    }
    // Remove the lineUp description from the targetName
    targetName = removeUnwantedWords(targetName, List.of(lineUp.getDescription().split("\\s+")));

    return new NameFinder(targetName, lineUp, false);
  }

  /**
   * Determines if the target name represents an Original Color Edition (OCE) figurine and cleans
   * the name accordingly. Searches for the word "Original" in the target name to identify OCE
   * figurines. If found, removes "Original", "Color", and "Edition" keywords from the name.
   *
   * @param targetName the original string to analyze for OCE indicators.
   * @return a NameFinder object with the cleaned target name, null lineup, and OCE flag set.
   */
  private NameFinder findOceUsing(String targetName) {
    boolean isOce = containsAnyWord(targetName, "Original");
    if (isOce) {
      // Remove the "Original", "Color" and "Edition" words from the targetName
      targetName = removeUnwantedWords(targetName, List.of("Original", "Color", "Edition"));
    }
    return new NameFinder(targetName, null, isOce);
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
   * Removes specified keywords from the target name string and cleans up whitespace. The removal is
   * case-insensitive and processes keywords in the order they appear in the list. After removal,
   * multiple consecutive spaces are collapsed to single spaces and the result is trimmed.
   *
   * @param targetName the original string from which keywords should be removed.
   * @param keywordsToRemove the list of keywords to remove from the target name.
   * @return the cleaned string with keywords removed and whitespace normalized.
   */
  private String removeUnwantedWords(String targetName, List<String> keywordsToRemove) {
    int startIndex;
    int endIndex;
    String result = targetName;
    for (String keyword : keywordsToRemove) {
      if (result.toLowerCase().contains(keyword.toLowerCase())) {
        if (keyword.equals("...")) {
          // when the keyword is "..." we need to remove the entire word
          int index = result.indexOf(keyword);
          startIndex = result.lastIndexOf(" ", index);
          endIndex = result.indexOf(" ", index + keyword.length());
          if (endIndex == -1) {
            endIndex = result.length();
          }
        } else {
          startIndex = result.toLowerCase().indexOf(keyword.toLowerCase());
          endIndex = startIndex + keyword.length();
        }
        result = result.substring(0, startIndex) + result.substring(endIndex);
      }
    }
    // Remove multiple spaces and trim the result
    result = result.replaceAll("\\s+", " ");

    log.info(
        "After removing keywords, the figurine: '{}' was converted to: '{}'",
        targetName,
        result.trim());
    return result.trim();
  }
}
