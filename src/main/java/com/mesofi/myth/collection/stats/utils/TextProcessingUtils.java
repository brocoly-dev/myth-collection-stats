package com.mesofi.myth.collection.stats.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextProcessingUtils {

  public static final String ELLIPSIS = "...";
  public static final String REGEX_SPACES = "\\s+";

  /**
   * Removes specified unwanted words from the input string and returns a cleaned version. This is a
   * convenience method that accepts a List of unwanted words instead of varargs.
   *
   * @param toBeModified the input string to be processed
   * @param unWantedWords list of words to be removed from the input string
   * @return a cleaned string with unwanted words removed and extra spaces trimmed
   */
  public static String removeWordsAndCleanup(String toBeModified, List<String> unWantedWords) {
    if (unWantedWords == null) {
      return toBeModified;
    }
    return removeWordsAndCleanup(toBeModified, unWantedWords.toArray(new String[0]));
  }

  /**
   * Removes specified unwanted words from the input string and returns a cleaned version. Words are
   * matched case-insensitively, except for ellipsis which uses contains matching.
   *
   * @param toBeModified the input string to be processed
   * @param unWantedWords variable number of words to be removed from the input string
   * @return a cleaned string with unwanted words removed and extra spaces trimmed
   */
  public static String removeWordsAndCleanup2(String toBeModified, String... unWantedWords) {
    if (toBeModified == null) {
      return null;
    }
    if (unWantedWords == null) {
      return toBeModified;
    }

    List<String> individualWords = new ArrayList<>(Arrays.asList(toBeModified.split(REGEX_SPACES)));
    for (String unwantedWord : Arrays.stream(unWantedWords).filter(Objects::nonNull).toList()) {
      if (unwantedWord.equals(ELLIPSIS)) {
        individualWords.removeIf(word -> word.contains(ELLIPSIS));
      } else {
        individualWords.removeIf(word -> word.equalsIgnoreCase(unwantedWord));
      }
    }
    return individualWords.stream().reduce("", (a, b) -> a + " " + b).trim();
  }

  public static String removeWordsAndCleanup(String toBeModified, String... unWantedWords) {
    if (toBeModified == null) {
      return null;
    }
    if (unWantedWords != null) {
      String unwantedWord;
      for (int i = 0; i < Arrays.stream(unWantedWords).filter(Objects::nonNull).toList().size(); ) {
        unwantedWord = unWantedWords[i];
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
   * Checks if the input string contains any of the specified words. Words are matched
   * case-insensitively and must be complete word matches (separated by whitespace).
   *
   * @param toBeTested the input string to be searched
   * @param someWords variable number of words to search for in the input string
   * @return true if any of the specified words are found in the input string, false otherwise
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
