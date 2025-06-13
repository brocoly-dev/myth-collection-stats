package com.mesofi.myth.collection.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.LineUp;
import com.mesofi.myth.collection.stats.config.StatsProp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FigurineMatchingServiceTest {

  @InjectMocks private FigurineMatchingService figurineMatchingService;

  @Mock StatsProp statsProp;

  private static final int MIN_MATCHING_DISTANCE = 10;
  private static final List<String> IGNORABLE_KEYWORDS =
      List.of(
          "Saint Seiya",
          "Bandai",
          "Version",
          "Japan",
          "Saint",
          "Myth",
          "Cloth",
          "Edition",
          "Surplice",
          "~",
          "...",
          "-",
          "(",
          ")");

  @ParameterizedTest
  @MethodSource("provideFigurineMatchingTestCases")
  void findFigurineBestMatch_shouldReturnExpectedFigurineForGivenInput(
      String inputString, Figurine expectedFigurine) {
    // Arrange
    when(statsProp.minMatchingDistance()).thenReturn(MIN_MATCHING_DISTANCE);
    when(statsProp.ignorableKeywords()).thenReturn(IGNORABLE_KEYWORDS);

    List<Figurine> figurineNames = createFigurines();

    // Act
    Optional<Figurine> result =
        figurineMatchingService.findFigurineBestMatch(figurineNames, inputString);

    // Assert
    assertThat(result).isPresent();
    assertFigurineEquals(result.get(), expectedFigurine);

    verify(statsProp, times(2)).minMatchingDistance();
    verify(statsProp).ignorableKeywords();
  }

  private static Stream<Arguments> provideFigurineMatchingTestCases() {
    return Stream.of(
        // https://www.lunapark.store/
        Arguments.of(
            "Bandai Saint Myth Cloth EX Griffon Minos Japan version",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH_EX, false)),
        Arguments.of(
            "Bandai Saint Myth Cloth EX Griffon Minos ORIGINAL COLOR EDITION Japan version",
            createExpectedFigurine(
                "Griffin Minos",
                "Griffin Minos ~Original Color Edition~",
                LineUp.MYTH_CLOTH_EX,
                true)),
        Arguments.of(
            "Bandai Saint Myth Cloth Griffon Minos Japan version",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH, false)),
        // https://www.yoyakunow.com/
        Arguments.of(
            "Myth Cloth EX Griffon Minos",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH_EX, false)),
        Arguments.of(
            "Myth Cloth EX Griffon Minos ~Original Color...",
            createExpectedFigurine(
                "Griffin Minos",
                "Griffin Minos ~Original Color Edition~",
                LineUp.MYTH_CLOTH_EX,
                true)),
        Arguments.of(
            "Myth Cloth Griffon Minos",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH, false)),
        // https://www.nin-nin-game.com/
        Arguments.of(
            "Saint Seiya Myth Cloth EX - Griffon Minos (Surplice)",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH_EX, false)),
        Arguments.of(
            "Saint Seiya Myth Cloth EX Minos Griffon Original Color",
            createExpectedFigurine(
                "Griffin Minos",
                "Griffin Minos ~Original Color Edition~",
                LineUp.MYTH_CLOTH_EX,
                true)),
        Arguments.of(
            "Saint Seiya Myth Cloth - Griffon Minos",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH, false)),
        Arguments.of(
            "Saint Seiya Myth Cloth - Griffon Minos [Used]",
            createExpectedFigurine("Griffin Minos", "Griffin Minos", LineUp.MYTH_CLOTH, false)));
  }

  private static Figurine createExpectedFigurine(
      String baseName, String displayableName, LineUp lineUp, boolean isOce) {
    Figurine figurine = new Figurine();
    figurine.setBaseName(baseName);
    figurine.setDisplayableName(displayableName);
    figurine.setLineUp(lineUp);
    figurine.setOce(isOce);
    return figurine;
  }

  private void assertFigurineEquals(Figurine actual, Figurine expected) {
    assertThat(actual.getBaseName()).isEqualTo(expected.getBaseName());
    assertThat(actual.getDisplayableName()).isEqualTo(expected.getDisplayableName());
    assertThat(actual.getLineUp()).isEqualTo(expected.getLineUp());
    assertThat(actual.isOce()).isEqualTo(expected.isOce());
  }

  private List<Figurine> createFigurines() {
    Figurine figurine1 = new Figurine();
    figurine1.setBaseName("Griffin Minos");
    figurine1.setDisplayableName("Griffin Minos");
    figurine1.setLineUp(LineUp.MYTH_CLOTH);
    figurine1.setOce(false);

    Figurine figurine2 = new Figurine();
    figurine2.setBaseName("Griffin Minos");
    figurine2.setDisplayableName("Griffin Minos ~Original Color Edition~");
    figurine2.setLineUp(LineUp.MYTH_CLOTH_EX);
    figurine2.setOce(true);

    Figurine figurine3 = new Figurine();
    figurine3.setBaseName("Griffin Minos");
    figurine3.setDisplayableName("Griffin Minos");
    figurine3.setLineUp(LineUp.MYTH_CLOTH_EX);
    figurine3.setOce(false);

    return Arrays.asList(figurine1, figurine2, figurine3);
  }
}
