package com.mesofi.myth.collection.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mesofi.myth.collection.core.mapper.FigurineMapper;
import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.SourceFigurine;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@Disabled("This test is only used to load the figurines into the database")
public class FigureMatchingTestSetupIT {

  private static final String CSV_FILE = "MythCloth Catalog - CatalogMyth.csv";
  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  private List<Figurine> figurines;

  @BeforeEach
  void loadAllFigurines() {
    final FigurineMapper figurineMapper = new FigurineMapper();

    Resource resource = new ClassPathResource(CSV_FILE);
    try (BufferedReader reader =
        Files.newBufferedReader(Paths.get(resource.getURI()), StandardCharsets.UTF_8)) {
      figurines =
          new CsvToBeanBuilder<SourceFigurine>(reader)
              .withType(SourceFigurine.class).build().parse().stream()
                  .map(figurineMapper::toFigure)
                  .toList();
    } catch (IOException e) {
      log.error("Unable to load figurines", e);
    }
  }

  @Test
  void createFigurinesJsonFiles() {
    List<String> figurineUniqueNames = new ArrayList<>();
    // Create path to src/test/resources directory
    String resourcesPath = "src/test/resources/figure-matching/figurines";
    File outputDir = new File(resourcesPath);

    for (int i = 0; i < figurines.size(); i++) {
      String name =
          (figurines.get(i).getBaseName().toLowerCase()).replaceAll(" ", "_")
              + "@"
              + figurines.get(i).getLineUp().toString().toLowerCase();
      if (figurineUniqueNames.contains(name)) {
        figurineUniqueNames.add(name + "_" + i);
        createFigurineJsonFile(outputDir, name + "_" + i, figurines.get(i));
      } else {
        figurineUniqueNames.add(name);
        createFigurineJsonFile(outputDir, name, figurines.get(i));
      }
    }
  }

  private void createFigurineJsonFile(File outputDir, String name, Figurine figurine) {
    try {
      File figurineFile = new File(outputDir, name + ".json");
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(figurineFile, figurine);
    } catch (IOException e) {
      log.error("Error creating JSON figurine", e);
    }
  }
}
