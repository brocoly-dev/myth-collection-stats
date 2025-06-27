package com.mesofi.myth.collection.stats.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TextProcessingUtilsTest {

  @Nested
  @DisplayName("removeWordsAndCleanup with List parameter")
  class RemoveWordsAndCleanupWithListTest {

    @Test
    @DisplayName("should return original string when unwanted words list is null")
    void shouldReturnOriginalStringWhenUnwantedWordsListIsNull() {
      String input = "Hello world test";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, (List<String>) null);
      assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("should remove words from list and cleanup")
    void shouldRemoveWordsFromListAndCleanup() {
      String input = "Hello world test example";
      List<String> unwantedWords = Arrays.asList("world", "test");
      String result = TextProcessingUtils.removeWordsAndCleanup(input, unwantedWords);
      assertThat(result).isEqualTo("Hello   example");
    }

    @Test
    @DisplayName("should handle empty list")
    void shouldHandleEmptyList() {
      String input = "Hello world";
      List<String> unwantedWords = Collections.emptyList();
      String result = TextProcessingUtils.removeWordsAndCleanup(input, unwantedWords);
      assertThat(result).isEqualTo("Hello world");
    }
  }

  @Nested
  @DisplayName("removeWordsAndCleanup with varargs parameter")
  class RemoveWordsAndCleanupWithVarargsTest {

    @Test
    @DisplayName("should return null when input is null")
    void shouldReturnNullWhenInputIsNull() {
      assertThat(TextProcessingUtils.removeWordsAndCleanup(null, "test")).isNull();
    }

    @Test
    @DisplayName("should return trimmed string when no unwanted words provided")
    void shouldReturnTrimmedStringWhenNoUnwantedWordsProvided() {
      String input = "  Hello world  ";
      String result = TextProcessingUtils.removeWordsAndCleanup(input);
      assertThat(result).isEqualTo("Hello world");
    }

    @Test
    @DisplayName("should remove single word case insensitive")
    void shouldRemoveSingleWordCaseInsensitive() {
      String input = "Hello WORLD test";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "world");
      assertThat(result).isEqualTo("Hello  test");
    }

    @Test
    @DisplayName("should remove multiple words")
    void shouldRemoveMultipleWords() {
      String input = "Hello world test example";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "world", "test");
      assertThat(result).isEqualTo("Hello   example");
    }

    @Test
    @DisplayName("should remove ellipsis and surrounding word")
    void shouldRemoveEllipsisAndSurroundingWord() {
      String input = "Hello world... test example";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "...");
      assertThat(result).isEqualTo("Hello test example");
    }

    @Test
    @DisplayName("should remove ellipsis at beginning of string")
    void shouldRemoveEllipsisAtBeginningOfString() {
      String input = "...hello world";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "...");
      assertThat(result).isEqualTo("world");
    }

    @Test
    @DisplayName("should remove ellipsis at end of string")
    void shouldRemoveEllipsisAtEndOfString() {
      String input = "hello world...";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "...");
      assertThat(result).isEqualTo("hello");
    }

    @Test
    @DisplayName("should remove ellipsis when it's the only content")
    void shouldRemoveEllipsisWhenItsTheOnlyContent() {
      String input = "...";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "...");
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle multiple occurrences of same word")
    void shouldHandleMultipleOccurrencesOfSameWord() {
      String input = "test hello test world test";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "test");
      assertThat(result).isEqualTo("hello  world");
    }

    @Test
    @DisplayName("should handle null values in unwanted words array")
    void shouldHandleNullValuesInUnwantedWordsArray() {
      String input = "Hello world test";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "world", null, "test");
      assertThat(result).isEqualTo("Hello");
    }

    @Test
    @DisplayName("should handle empty string input")
    void shouldHandleEmptyStringInput() {
      String input = "";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "test");
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle word that doesn't exist in string")
    void shouldHandleWordThatDoesntExistInString() {
      String input = "Hello world";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "nonexistent");
      assertThat(result).isEqualTo("Hello world");
    }

    @Test
    @DisplayName("should handle partial word matches")
    void shouldHandlePartialWordMatches() {
      String input = "Hello world";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "wor");
      assertThat(result).isEqualTo("Hello ld");
    }
  }

  @Nested
  @DisplayName("containsAnyWords")
  class ContainsAnyWordsTest {

    @Test
    @DisplayName("should return false when input is null")
    void shouldReturnFalseWhenInputIsNull() {
      assertThat(TextProcessingUtils.containsAnyWords(null, "test")).isFalse();
    }

    @Test
    @DisplayName("should return true when string contains one of the words")
    void shouldReturnTrueWhenStringContainsOneOfTheWords() {
      String input = "Hello world test";
      boolean result = TextProcessingUtils.containsAnyWords(input, "world", "example");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when string contains none of the words")
    void shouldReturnFalseWhenStringContainsNoneOfTheWords() {
      String input = "Hello world test";
      boolean result = TextProcessingUtils.containsAnyWords(input, "example", "sample");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should be case insensitive")
    void shouldBeCaseInsensitive() {
      String input = "Hello WORLD test";
      boolean result = TextProcessingUtils.containsAnyWords(input, "world");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return true for partial matches")
    void shouldReturnTrueForPartialMatches() {
      String input = "Hello world test";
      boolean result = TextProcessingUtils.containsAnyWords(input, "wor");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should handle empty string input")
    void shouldHandleEmptyStringInput() {
      String input = "";
      boolean result = TextProcessingUtils.containsAnyWords(input, "test");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should handle no search words")
    void shouldHandleNoSearchWords() {
      String input = "Hello world";
      boolean result = TextProcessingUtils.containsAnyWords(input);
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return true when multiple words match")
    void shouldReturnTrueWhenMultipleWordsMatch() {
      String input = "Hello world test example";
      boolean result = TextProcessingUtils.containsAnyWords(input, "world", "test");
      assertThat(result).isTrue();
    }
  }
}
