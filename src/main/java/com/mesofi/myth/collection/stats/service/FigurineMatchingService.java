package com.mesofi.myth.collection.stats.service;

import static com.mesofi.myth.collection.stats.utils.TextProcessingUtils.containsAnyWords;
import static com.mesofi.myth.collection.stats.utils.TextProcessingUtils.removeWordsAndCleanup;

import com.mesofi.myth.collection.core.model.*;
import com.mesofi.myth.collection.stats.config.StatsProp;
import com.mesofi.myth.collection.stats.model.AttributeExtractionResult;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FigurineMatchingService {

  private static final String REVIVAL = "Revival";
  private static final String ORIGINAL = "Original";
  private static final String COLOR = "Color";
  private static final String EDITION = "Edition";
  private static final String OCE = "O.C.E.";
  private static final String GOLDEN = "Golden";
  private static final String POG = "Power of Gold";
  private static final String SET = "Set";
  private static final String HK = "HK";
  private static final String HONK_KONG = "Hong Kong";
  private static final String EX = "ex";
  private static final String GOD_CLOTH = "God Cloth";
  private static final String SUCCESSOR = "Successor";
  private static final String NEW_BRONZE_CLOTH = "New Bronze Cloth";
  private static final String FIRST_BRONZE_CLOTH = "First Bronze Cloth";
  private static final String _10TH = "10th";
  private static final String _20TH = "20th";

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
   * Finds the best matching figurine from a collection based on the store figurine name and store
   * context. This method performs comprehensive attribute extraction and filtering to identify the
   * most suitable match.
   *
   * <p>The matching process involves: 1. Extracting lineup information from the figurine name
   * (mandatory for matching) 2. Identifying categories present in the name (mandatory for matching)
   * 3. Detecting special attributes: Revival, OCE (Original Color Edition), Golden, and Set flags
   * 4. Filtering the figurine collection based on extracted attributes 5. Using Levenshtein
   * distance algorithm to find the closest name match 6. Validating the match against the
   * configured minimum distance threshold
   *
   * @param figurines a non-empty list of figurines to search through for potential matches
   * @param storeFigurineName a non-empty string representing the figurine name from the store
   * @param store the store context for the figurine matching operation
   * @return an Optional containing the best matching Figurine if found and within distance
   *     threshold, or empty Optional if no suitable match is found or distance exceeds threshold
   */
  public Optional<Figurine> findFigurineBestMatch(
      @NotEmpty List<Figurine> figurines,
      @NotEmpty String storeFigurineName,
      @NotNull Store store) {

    // Filter figurines based on release date
    List<Figurine> filteredFigurines =
        figurines.stream()
            .filter(this::withValidReleaseDate)
            .sorted(Comparator.comparing(f -> f.getDistributionJPY().getReleaseDate()))
            .toList();
    if (filteredFigurines.isEmpty()) {
      return Optional.empty();
    }

    // Preprocess the store figurine name by removing ignorable keywords
    String filteredName = removeWordsAndCleanup(storeFigurineName, statsProp.ignorableKeywords());

    // LineUp is mandatory for figurine matching
    AttributeExtractionResult attrLineUp = containsLineUp(filteredName);
    LineUp lineUpFound = attrLineUp.lineUp();
    filteredName = attrLineUp.filteredName();
    // Categories are mandatory for figurine matching
    AttributeExtractionResult attrCategory = containsCategory(filteredName);
    List<Category> categoriesFound = attrCategory.categoryList();
    filteredName = attrCategory.filteredName();
    // Revival is mandatory for figurine matching
    AttributeExtractionResult attrRevival = contains(filteredName, REVIVAL);
    boolean isRevival = attrRevival.attribute();
    filteredName = attrRevival.filteredName();
    // OCE is mandatory for figurine matching
    AttributeExtractionResult attrOce = contains(filteredName, ORIGINAL, COLOR, OCE);
    boolean isOce = attrOce.attribute();
    filteredName = attrOce.filteredName();
    // Golden is mandatory for figurine matching
    AttributeExtractionResult attrGolden = contains(filteredName, GOLDEN, POG);
    boolean golden = attrGolden.attribute();
    filteredName = attrGolden.filteredName();
    // Set is mandatory for figurine matching
    AttributeExtractionResult attrSet = contains(filteredName, SET);
    boolean set = attrSet.attribute();
    filteredName = attrSet.filteredName();
    // HK is mandatory for figurine matching
    AttributeExtractionResult attrHK = contains(filteredName, HK, HONK_KONG);
    boolean hk = attrSet.attribute();
    filteredName = attrSet.filteredName();
    // Anniversary is mandatory for figurine matching
    AttributeExtractionResult attrAnniversary = containsAnniversary(filteredName);
    Anniversary anniversary = attrAnniversary.anniversary();
    filteredName = attrAnniversary.filteredName();

    // Just make sure we don't have any extra spaces
    filteredName = filteredName.replaceAll("\\s+", " ");

    LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
    int dist;
    int minDistance = Integer.MAX_VALUE;
    Figurine bestMatchFigurine = null;
    for (Figurine f :
        filteredFigurines.stream()
            .filter(f -> f.getLineUp() == lineUpFound)
            .filter(
                f -> {
                  if (categoriesFound.isEmpty()) {
                    return true;
                  }
                  return categoriesFound.contains(f.getCategory());
                })
            .filter(f -> f.isRevival() == isRevival)
            .filter(f -> f.isOce() == isOce)
            .filter(f -> f.isGolden() == golden)
            .filter(f -> f.isSet() == set)
            .filter(f -> f.isHk() == hk)
            .filter(
                f -> {
                  if (anniversary == null) {
                    return true;
                  }
                  return f.getAnniversary() == anniversary;
                })
            .toList()) {
      // Use Levenshtein distance on the base name
      dist = levenshtein.apply(filteredName.toLowerCase(), f.getBaseName().toLowerCase());
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
            filteredName,
            minDistance,
            statsProp.minMatchingDistance());
        return Optional.empty();
      } else {
        log.info(
            "The best match for figurine: '{}' is: '{}', minDistance: {}, expectedMinDistance: {}",
            filteredName,
            bestOptionalMatchFigurine.get().getBaseName(),
            minDistance,
            statsProp.minMatchingDistance());
        return bestOptionalMatchFigurine;
      }
    } else {
      log.warn("Unable to find figurine for: '{}' in invalid dataset", filteredName);
      return Optional.empty();
    }
  }

  /**
   * Validates whether the given figurine has a valid release date in its Japanese distribution. A
   * figurine is considered to have a valid release date if it has a non-null distribution for the
   * Japanese market (JPY) and that distribution contains a non-null release date.
   *
   * @param figurine the figurine to validate
   * @return true if the figurine has a valid Japanese release date, false otherwise
   */
  private boolean withValidReleaseDate(Figurine figurine) {
    return Optional.ofNullable(figurine.getDistributionJPY())
        .map(distribution -> Objects.nonNull(distribution.getReleaseDate()))
        .orElse(false);
  }

  /**
   * Analyzes the given filtered name for lineup-specific keywords and determines the appropriate
   * lineup. Currently, detects "ex" keywords and maps them to MYTH_CLOTH_EX lineup, otherwise
   * defaults to MYTH_CLOTH.
   *
   * @param filteredName the name to analyze for lineup-specific keywords
   * @return an AttributeExtractionResult containing the processed name (with lineup keywords
   *     removed if found), the detected lineup, null categories, and false for the found flag
   */
  private AttributeExtractionResult containsLineUp(String filteredName) {
    LineUp lineUpFound = LineUp.MYTH_CLOTH;

    String[] keywords = {EX};
    if (containsAnyWords(filteredName, keywords)) {
      lineUpFound = LineUp.MYTH_CLOTH_EX;
      filteredName = removeWordsAndCleanup(filteredName, keywords);
    }

    return new AttributeExtractionResult(filteredName, lineUpFound, null, false, null);
  }

  /**
   * Analyzes the given filtered name for category-specific keywords and extracts relevant
   * categories. Currently, detects "God Cloth" keywords and maps them to V4 and GOLD categories.
   *
   * @param filteredName the name to analyze for category-specific keywords
   * @return an AttributeExtractionResult containing the processed name (with category keywords
   *     removed if found), null lineup, a list of detected categories, and false for the found flag
   */
  private AttributeExtractionResult containsCategory(String filteredName) {
    List<Category> categories = new ArrayList<>();

    String[] keywordsFirstBronzeCloth = {FIRST_BRONZE_CLOTH};
    if (containsAnyWords(filteredName, keywordsFirstBronzeCloth)) {
      categories.add(Category.V1);
      filteredName = removeWordsAndCleanup(filteredName, keywordsFirstBronzeCloth);
    }
    String[] keywordsNewBronzeCloth = {NEW_BRONZE_CLOTH};
    if (containsAnyWords(filteredName, keywordsNewBronzeCloth)) {
      categories.add(Category.V2);
      filteredName = removeWordsAndCleanup(filteredName, keywordsNewBronzeCloth);
    }
    String[] keywordsGodCloth = {GOD_CLOTH};
    if (containsAnyWords(filteredName, keywordsGodCloth)) {
      categories.add(Category.V4);
      categories.add(Category.GOLD);
      filteredName = removeWordsAndCleanup(filteredName, keywordsGodCloth);
    }
    String[] keywordsInheritor = {SUCCESSOR};
    if (containsAnyWords(filteredName, keywordsInheritor)) {
      categories.add(Category.INHERITOR);
      filteredName = removeWordsAndCleanup(filteredName, keywordsInheritor);
    }
    // Special case for POG
    String[] keywordsPog = {POG};
    if (containsAnyWords(filteredName, keywordsPog)) {
      categories.add(Category.V2);
    }

    return new AttributeExtractionResult(filteredName, null, categories, false, null);
  }

  private AttributeExtractionResult containsAnniversary(String filteredName) {
    String[] keywords10Anniversary = {_10TH};
    Anniversary anniversary = null;
    if (containsAnyWords(filteredName, keywords10Anniversary)) {
      anniversary = Anniversary.A_10;
      filteredName = removeWordsAndCleanup(filteredName, keywords10Anniversary);
    }
    String[] keywords20Anniversary = {_20TH};
    if (containsAnyWords(filteredName, keywords20Anniversary)) {
      anniversary = Anniversary.A_20;
      filteredName = removeWordsAndCleanup(filteredName, keywords20Anniversary);
    }
    return new AttributeExtractionResult(filteredName, null, null, false, anniversary);
  }

  /**
   * Checks if the given filtered name contains any of the specified attributes and removes them if
   * found. This is a generic utility method for attribute detection and extraction from figurine
   * names.
   *
   * @param filteredName the name to analyze for attribute presence
   * @param attributes variable number of attribute strings to search for in the filtered name
   * @return an AttributeExtractionResult containing the processed name (with attributes removed if
   *     found), null lineup and categories, and a boolean indicating whether any attributes were
   *     found and removed
   */
  private AttributeExtractionResult contains(String filteredName, String... attributes) {
    boolean found = containsAnyWords(filteredName, attributes);
    String resultName = found ? removeWordsAndCleanup(filteredName, attributes) : filteredName;

    return new AttributeExtractionResult(resultName, null, null, found, null);
  }
}
