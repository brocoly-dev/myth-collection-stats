package com.mesofi.myth.collection.stats.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LunaParkScraperServiceTest {

  @Autowired private StoreScraperService scraperService;

  @Test
  void testScrapeFigurines() {
    // This is an integration test that actually calls the website
    // Consider mocking for unit tests
    // List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.LUNA_PARK);
    // List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.HOBBY_GENKI);
    // List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.NIN_NIN_GAME);
    // List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.MANDARAKE);
    List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.JUNGLE);

    assertThat(figurines).isNotNull();
    // The website should have some results for "Myth cloth"
    assertThat(figurines).isNotEmpty();

    // Verify that we extracted meaningful data
    StoreFigurineInfo firstFigurine = figurines.get(0);
    assertThat(firstFigurine.getRawName()).isNotBlank();

    // Log results for manual verification
    figurines.forEach(
        figurine ->
            System.out.printf(
                "Name: %s, Price: %s, URL: %s%n",
                figurine.getRawName(), figurine.getRetailPrice(), figurine.getProductUrl()));
    System.out.println("Total figurines found: " + figurines.size());
  }
}
