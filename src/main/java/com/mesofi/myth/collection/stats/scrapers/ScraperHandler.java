package com.mesofi.myth.collection.stats.scrapers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Handler interface for scraping figurine information from online stores. Implementations of this
 * interface define how to extract product data from specific store websites using web scraping
 * techniques.
 */
public interface ScraperHandler {

  /**
   * Returns the store associated with this scraper handler.
   *
   * @return the Store enum value representing the online store this handler scrapes
   */
  Store getStore();

  /**
   * Returns the base search URL for the store website.
   *
   * @return the search URL string used to query products on the store's website
   */
  String getSearchUrl();

  /**
   * Extracts product item elements from the parsed HTML document.
   *
   * @param document the parsed HTML document from the store's search results page
   * @return Elements collection containing the individual product items found on the page
   */
  Elements getProductItems(Document document);

  /**
   * Extracts figurine information from a product item element.
   *
   * @param item the HTML element representing a single product item from the store's search results
   * @return Optional containing StoreFigurineInfo if extraction is successful, empty otherwise
   */
  Optional<StoreFigurineInfo> extractFigurineInfo(Element item);

  /**
   * Returns the currency used by the store for pricing.
   *
   * @return the Currency instance, defaults to Japanese Yen
   */
  default Currency getCurrency() {
    return Currency.getInstance(Locale.JAPAN);
  }

  /**
   * Returns custom HTTP headers to be used when making requests to the store website.
   *
   * @return a Map containing custom header names as keys and their values, empty by default
   */
  default Map<String, String> customHeaders() {
    return Map.of();
  }
}
