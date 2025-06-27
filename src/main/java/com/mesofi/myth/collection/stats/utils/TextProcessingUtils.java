package com.mesofi.myth.collection.stats.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for text processing operations including word removal and text analysis. Provides
 * static methods for cleaning up strings by removing unwanted words and checking for word
 * containment with case-insensitive matching.
 *
 * <p>This class handles special processing for ellipsis ("...") by removing entire words containing
 * the ellipsis pattern, while other unwanted words are removed through exact case-insensitive
 * matching with iterative cleanup until all occurrences are eliminated.
 *
 * <p>All methods in this utility class are static and thread-safe.
 */
@Slf4j
public class TextProcessingUtils {
  // Constants
  public static final String ELLIPSIS = "...";

  /**
   * Removes specified unwanted words from the given string and cleans up the result. This is a
   * convenience method that accepts a List of unwanted words and delegates to the varargs version
   * of removeWordsAndCleanup.
   *
   * @param toBeModified the string to be modified by removing unwanted words
   * @param unWantedWords the list of words to be removed from the string
   * @return the modified string with unwanted words removed and trimmed, or the original string if
   *     unWantedWords is null
   */
  public static String removeWordsAndCleanup(String toBeModified, List<String> unWantedWords) {
    if (unWantedWords == null) {
      return toBeModified;
    }
    return removeWordsAndCleanup(toBeModified, unWantedWords.toArray(new String[0]));
  }

  /**
   * Removes specified unwanted words from the given string and cleans up the result. For ellipsis
   * ("..."), removes the entire word containing the ellipsis along with surrounding spaces. For
   * other words, removes exact matches (case-insensitive) and continues until all occurrences are
   * removed.
   *
   * @param toBeModified the string to be modified by removing unwanted words
   * @param unWantedWords the words to be removed from the string
   * @return the modified string with unwanted words removed and trimmed, or null if input is null
   */
  public static String removeWordsAndCleanup(String toBeModified, String... unWantedWords) {
    if (toBeModified == null) {
      return null;
    }
    if (unWantedWords != null) {
      String unwantedWord;

      List<String> unWantedWordList =
          Arrays.stream(unWantedWords).filter(Objects::nonNull).toList();
      System.out.println("unWantedWords: " + Arrays.stream(unWantedWords));

      for (int i = 0; i < unWantedWordList.size(); ) {
        unwantedWord = unWantedWordList.get(i);
        if (unwantedWord.equals(ELLIPSIS)) {
          int ellipsisIndex = toBeModified.indexOf(ELLIPSIS);
          if (ellipsisIndex != -1) {
            int initIndex = 0;
            int endIndex;
            if (ellipsisIndex != 0) {
              initIndex = toBeModified.lastIndexOf(" ", ellipsisIndex);
              if (initIndex == -1) {
                initIndex = 0;
              }
            }
            endIndex = toBeModified.indexOf(" ", ellipsisIndex);
            if (endIndex == -1) {
              endIndex = toBeModified.length();
            }
            toBeModified = toBeModified.substring(0, initIndex) + toBeModified.substring(endIndex);
          }
        } else {
          int initIndex = toBeModified.toLowerCase().indexOf(unwantedWord.toLowerCase());
          int endIndex;
          if (initIndex != -1) {
            endIndex = initIndex + unwantedWord.length();
            toBeModified = toBeModified.substring(0, initIndex) + toBeModified.substring(endIndex);
          }
        }
        if (toBeModified.contains(unwantedWord)) {
          continue;
        }
        i++;
      }
    }

    return toBeModified.trim();
  }

  /**
   * Checks if the given string contains any of the specified words (case-insensitive).
   *
   * @param toBeTested the string to be tested for containing any of the words
   * @param someWords the words to search for in the string
   * @return true if the string contains any of the specified words, false otherwise
   */
  public static boolean containsAnyWords(String toBeTested, String... someWords) {
    if (toBeTested == null) {
      return false;
    }
    for (String word : someWords) {
      if (toBeTested.toLowerCase().contains(word.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}
