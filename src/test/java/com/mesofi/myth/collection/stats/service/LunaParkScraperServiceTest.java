package com.mesofi.myth.collection.stats.service;

import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.LineUp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LunaParkScraperServiceTest {

  @Autowired private StoreScraperService scraperService;

  @Test
  void testScrapeFigurines() {
    // This is an integration test that actually calls the website

    // List<StoreFigurineInfo> figurines = scraperService.findAllFigurines(Store.NIN_NIN_GAME);
    // figurines.forEach(System.out::println);

    Figurine figurine1 = new Figurine();
    figurine1.setLineUp(LineUp.APPENDIX);
    figurine1.setBaseName("a");
    Figurine figurine2 = new Figurine();
    figurine2.setLineUp(LineUp.MYTH_CLOTH);
    figurine2.setBaseName("a");

    System.out.println(figurine1.equals(figurine2));
  }
}
