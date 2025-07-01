package com.mesofi.myth.collection.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mesofi.myth.collection.core.mapper.FigurineMapper;
import com.mesofi.myth.collection.core.model.Figurine;
import com.mesofi.myth.collection.core.model.SourceFigurine;
import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.config.StatsProp;
import com.mesofi.myth.collection.stats.service.FigurineMatchingService;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = {FigurineMatchingService.class})
@EnableConfigurationProperties(StatsProp.class)
public class FigureMatchingIT {

  private static final String CSV_FILE = "MythCloth Catalog - CatalogMyth.csv";
  private static final String YAML_FILE = "figure-matching/stores/name-figurine.yml";
  private static final String JSON_PATH = "figure-matching/figurines/";

  private static final ObjectMapper objectMapper =
      new ObjectMapper().registerModule(new JavaTimeModule());
  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private static List<Figurine> figurines;
  private static Map<String, List<String>> nameFigurineMapping;

  @Autowired private FigurineMatchingService figurineMatchingService;

  @BeforeAll
  static void loadAllFigurines() {
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
    // Load YAML file
    loadNameFigurineMapping();
  }

  private static void loadNameFigurineMapping() {
    Resource yamlResource = new ClassPathResource(YAML_FILE);
    try (InputStream inputStream = yamlResource.getInputStream()) {
      nameFigurineMapping = yamlMapper.readValue(inputStream, new TypeReference<>() {});
      log.info("Loaded {} entries from YAML file", nameFigurineMapping.size());
    } catch (IOException e) {
      log.error("Unable to load YAML file: {}", YAML_FILE, e);
      nameFigurineMapping = Map.of(); // Initialize as empty map
    }
  }

  @ParameterizedTest
  @MethodSource("provideStringsForIsBlank")
  void findFigurineBestMatch(Figurine expectedFigurine, List<String> storeFigurineNames) {

    Optional<Figurine> figurineFound;
    for (String storeFigurineName : storeFigurineNames) {
      figurineFound =
          figurineMatchingService.findFigurineBestMatch(
              figurines, storeFigurineName, Store.ANIME_EXPORT);
      assertTrue(figurineFound.isPresent());
      assertEquals(expectedFigurine, figurineFound.get());
    }
  }

  private static Stream<Arguments> provideStringsForIsBlank() {
    List<Arguments> arguments = new ArrayList<>();

    nameFigurineMapping.forEach(
        (jsonFilename, storeNames) -> {
          Figurine expectedFigurine = toFigurine(new ClassPathResource(JSON_PATH + jsonFilename));
          arguments.add(Arguments.of(expectedFigurine, storeNames));
        });

    return arguments.stream();
  }

  private static Figurine toFigurine(Resource jsonResource) {
    try {
      return objectMapper.readValue(jsonResource.getInputStream(), Figurine.class);
    } catch (IOException e) {
      log.error("Unable to create a figurine", e);
      throw new RuntimeException(e);
    }
  }
}
