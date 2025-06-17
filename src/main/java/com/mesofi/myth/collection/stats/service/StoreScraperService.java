package com.mesofi.myth.collection.stats.service;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.config.StatsProp;
import com.mesofi.myth.collection.stats.exception.ScraperException;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandlerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoreScraperService {

  /** The statistics properties configuration used for figurine matching operations. */
  private final StatsProp statsProp;

  private final ScraperHandlerFactory scraperFactory;

  public StoreScraperService(StatsProp statsProp, ScraperHandlerFactory scraperFactory) {
    this.statsProp = statsProp;
    this.scraperFactory = scraperFactory;
  }

  public List<StoreFigurineInfo> findAllFigurines(Store store) {
    List<StoreFigurineInfo> allFigurines = new ArrayList<>();
    List<StoreFigurineInfo> figurines;
    for (int i = 1; ; i++) {
      figurines = scrapeFigurines(store, i);
      if (figurines.isEmpty()) {
        break;
      } else {
        allFigurines.addAll(figurines);
      }
    }
    log.info("Found {} figurines at {} store", allFigurines.size(), store);
    return allFigurines;
  }

  @Retryable(
      retryFor = {ScraperException.class},
      backoff = @Backoff(delay = 2000, multiplier = 2))
  public List<StoreFigurineInfo> scrapeFigurines(Store store, int pageNumber) {
    List<StoreFigurineInfo> figurines = new ArrayList<>();
    ScraperHandler scraperHandler = scraperFactory.getHandler(store);

    String searchUrl =
        scraperHandler.getSearchBaseUrl() + scraperHandler.getSearchContextUrl() + pageNumber;
    try {
      log.info("Starting to scrape {} website: {}", scraperHandler.getStore(), searchUrl);

      Document document =
          Jsoup.connect(searchUrl)
              .timeout(statsProp.timeout())
              .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
              .headers(scraperHandler.customHeaders())
              .get();

      Elements productItems = scraperHandler.getFigurineElements(document);

      log.info("Found {} product items", productItems.size());

      for (Element item : productItems) {
        try {
          scraperHandler.extractFigurineInfo(item).ifPresent(figurines::add);
        } catch (Exception e) {
          log.warn("Error extracting figurine info from item: {}", e.getMessage());
        }
      }

      log.info("Successfully scraped {} figurines", figurines.size());
      return figurines;
    } catch (IOException e) {
      log.warn("Error scraping {} website: {}", store, e.getMessage());
      throw new ScraperException(e.getMessage()); // throw to trigger retry
    }
  }

  @Recover
  public List<StoreFigurineInfo> recover(RuntimeException ex, int pageNumber) {
    if (ex instanceof ScraperException) {
      log.error("All retry attempts failed for page {}: {}", pageNumber, ex.getMessage());
      return new ArrayList<>(); // Return empty list as fallback
    }
    throw ex;
  }
}
