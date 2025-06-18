package com.mesofi.myth.collection.stats;

import com.mesofi.myth.collection.core.mapper.FigurineMapper;
import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.SourceFigurine;
import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.config.StatsProp;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandlerFactory;
import com.mesofi.myth.collection.stats.service.FigurineMatchingService;
import com.mesofi.myth.collection.stats.service.StoreScraperService;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SpringBootTest(
    classes = {
      FigurineMatchingService.class,
      StoreScraperService.class,
      ScraperHandlerFactory.class
    })
@EnableConfigurationProperties(StatsProp.class)
public class FigureMatchingIT {

  @Autowired StatsProp statsProp;

  @Autowired FigurineMatchingService figurineMatchingService;
  @Autowired StoreScraperService storeScraperService;

  List<Figurine> figurines;

  @BeforeEach
  void setup() throws IOException {
    final FigurineMapper figurineMapper = new FigurineMapper();

    Resource resource = new ClassPathResource("MythCloth Catalog - CatalogMyth.csv");

    BufferedReader reader =
        Files.newBufferedReader(Paths.get(resource.getURI()), StandardCharsets.UTF_8);

    figurines =
        new CsvToBeanBuilder<SourceFigurine>(reader)
            .withType(SourceFigurine.class).build().parse().stream()
                .map($ -> figurineMapper.toFigure($))
                .toList();
  }

  @Test
  void ss() {
    System.out.println("================++++++++");
    System.out.println(figurines);
    System.out.println(figurines.size());

    Set<Figurine> s = new HashSet<>(figurines);
    System.out.println(s.size());

    List<StoreFigurineInfo> list = storeScraperService.findAllFigurines(Store.HOBBY_GENKI);

    Optional<Figurine> found =
        figurineMatchingService.findFigurineBestMatch(figurines, list.get(0).getRawName());
    System.out.println(found);
  }
}
