package com.mesofi.myth.collection.stats.service;

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

    List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.ANIME_EXPORT);
    for (StoreFigurineInfo storeFigurineInfo : figurines) {
      System.out.println(storeFigurineInfo.getRawName());
    }
  }
}
