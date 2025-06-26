package com.mesofi.myth.collection.stats.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TextProcessingUtilsTest {

  @Nested
  @DisplayName("removeWordsAndCleanup tests")
  class RemoveWordsAndCleanupTests {

    @Test
    @DisplayName("should return null when input string is null")
    void shouldReturnNullWhenInputIsNull() {
      assertThat(TextProcessingUtils.removeWordsAndCleanup(null, "word")).isNull();
    }

    @Test
    @DisplayName("should return original string when unwanted words array is null")
    void shouldReturnOriginalStringWhenUnwantedWordsIsNull() {
      String input = "hello world";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, (String[]) null);
      assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("should return original string when no unwanted words provided")
    void shouldReturnOriginalStringWhenNoUnwantedWords() {
      String input = "hello world";
      String result = TextProcessingUtils.removeWordsAndCleanup(input);
      assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("should remove single word case-insensitively")
    void shouldRemoveSingleWordCaseInsensitively() {
      String input = "Hello World Test";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "hello");
      assertThat(result).isEqualTo("World Test");
    }

    @Test
    @DisplayName("should remove multiple words")
    void shouldRemoveMultipleWords() {
      String input = "Hello World Test Example";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "hello", "test");
      assertThat(result).isEqualTo("World Example");
    }

    @Test
    @DisplayName("should handle ellipsis removal with contains matching")
    void shouldHandleEllipsisRemoval() {
      String input = "Hello World... Test more...text";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "...");
      assertThat(result).isEqualTo("Hello Test");
    }

    @Test
    @DisplayName("should clean up extra spaces")
    void shouldCleanUpExtraSpaces() {
      String input = "  Hello   World  Test  ";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "world");
      assertThat(result).isEqualTo("Hello Test");
    }

    @Test
    @DisplayName("should return empty string when all words are removed")
    void shouldReturnEmptyStringWhenAllWordsRemoved() {
      String input = "Hello World";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "hello", "world");
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle empty input string")
    void shouldHandleEmptyInputString() {
      String result = TextProcessingUtils.removeWordsAndCleanup("", "word");
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should ignore null unwanted words in array")
    void shouldIgnoreNullUnwantedWords() {
      String input = "Hello World Test";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "hello", null, "test");
      assertThat(result).isEqualTo("World");
    }

    @Test
    @DisplayName("should handle mixed case ellipsis and regular words")
    void shouldHandleMixedCaseEllipsisAndRegularWords() {
      String input = "Hello World... Test EXAMPLE";
      String result = TextProcessingUtils.removeWordsAndCleanup(input, "...", "example");
      assertThat(result).isEqualTo("Hello Test");
    }
  }

  @Nested
  @DisplayName("containsAnyWords tests")
  class ContainsAnyWordsTests {

    @Test
    @DisplayName("should return false when input string is null")
    void shouldReturnFalseWhenInputIsNull() {
      assertThat(TextProcessingUtils.containsAnyWords(null, "word")).isFalse();
    }

    @Test
    @DisplayName("should return false when no search words provided")
    void shouldReturnFalseWhenNoSearchWords() {
      boolean result = TextProcessingUtils.containsAnyWords("hello world");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return true when single word found case-insensitively")
    void shouldReturnTrueWhenSingleWordFoundCaseInsensitively() {
      boolean result = TextProcessingUtils.containsAnyWords("Hello World Test", "hello");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return true when any of multiple words found")
    void shouldReturnTrueWhenAnyOfMultipleWordsFound() {
      boolean result =
          TextProcessingUtils.containsAnyWords("Hello World Test", "missing", "world", "absent");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false when no words found")
    void shouldReturnFalseWhenNoWordsFound() {
      boolean result =
          TextProcessingUtils.containsAnyWords("Hello World Test", "missing", "absent");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should match complete words only")
    void shouldMatchCompleteWordsOnly() {
      boolean result = TextProcessingUtils.containsAnyWords("Hello World Test", "Wor");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should handle empty input string")
    void shouldHandleEmptyInputString() {
      boolean result = TextProcessingUtils.containsAnyWords("", "word");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should handle multiple spaces in input")
    void shouldHandleMultipleSpacesInInput() {
      boolean result = TextProcessingUtils.containsAnyWords("  Hello   World  Test  ", "world");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should handle mixed case matching")
    void shouldHandleMixedCaseMatching() {
      boolean result = TextProcessingUtils.containsAnyWords("HELLO world TeSt", "World", "MISSING");
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false for partial word matches")
    void shouldReturnFalseForPartialWordMatches() {
      boolean result = TextProcessingUtils.containsAnyWords("Testing HelloWorld", "Hello", "World");
      assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should handle single character words")
    void shouldHandleSingleCharacterWords() {
      boolean result = TextProcessingUtils.containsAnyWords("a b c d", "b");
      assertThat(result).isTrue();
    }
  }
}
