package com.mesofi.myth.collection.stats.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.stats.config.StatsProp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FigurineMatchingServiceTest {

  @InjectMocks private FigurineMatchingService figurineMatchingService;

  @Mock StatsProp statsProp;

  private static final int MIN_MATCHING_DISTANCE = 10;
  private static final List<String> IGNORABLE_KEYWORDS =
      List.of("Bandai", "Version", "Japan", "Saint", "Myth", "Cloth");

  @Test
  void findFigurineBestMatch_shouldReturnExactMatchWhenFindingFigurineBestMatchWithExactMatch() {

    when(statsProp.minMatchingDistance()).thenReturn(MIN_MATCHING_DISTANCE);
    when(statsProp.ignorableKeywords()).thenReturn(IGNORABLE_KEYWORDS);

    Figurine figurine1 = new Figurine();
    figurine1.setDisplayableName("Griffin Minos");
    Figurine figurine2 = new Figurine();
    figurine2.setDisplayableName("Griffin Minos ~Original Color Edition~");

    List<Figurine> figurineNames = Arrays.asList(figurine1, figurine2);
    String targetName = "Bandai Saint Myth Cloth EX Griffon Minos Japan version";

    Optional<Figurine> result =
        figurineMatchingService.findFigurineBestMatch(figurineNames, targetName);

    assertThat(result).isPresent();
    assertThat(result.get().getDisplayableName()).isEqualTo("Griffin Minos");

    verify(statsProp, times(2)).minMatchingDistance();
    verify(statsProp).ignorableKeywords();
  }
}
