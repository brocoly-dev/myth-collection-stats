package com.mesofi.myth.collection.stats.scrapers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Handles the processing and management of scraped data from various sources. This class provides
 * functionality to extract, validate, and transform scraped content into structured data formats.
 */
public interface ScraperDataHandler {

  /**
   * Removes unwanted products from the given list of product items.
   *
   * @param productItems The list of product items to filter.
   */
  default void removeUnwantedProducts(Elements productItems) {
    // Filter out the results that don't contain "Myth cloth" in the name
    productItems.removeIf(
        productItem ->
            findFigurineName(productItem)
                .map(
                    name ->
                        !name.toLowerCase().contains("myth cloth")
                            && !name.toLowerCase().contains("cloth myth"))
                .orElse(true));
  }

  /**
   * Extracts the figurine name from the given item.
   *
   * @param item The item to extract the figurine name from.
   * @return An optional containing the figurine name, or an empty optional if the name could not
   *     be.
   */
  Optional<String> findFigurineName(Element item);

  /**
   * Parses the price from the given price text.
   *
   * @param currency The currency of the price.
   * @param priceText The price text to parse.
   * @return The parsed price, or null if the price could not be parsed.
   */
  default BigDecimal parsePrice(Currency currency, String priceText) {
    if (priceText == null || priceText.isEmpty()) {
      return null;
    }
    String symbol = currency.getSymbol();
    // Remove currency symbol and commas, extract numbers
    String cleanPrice = priceText.replaceAll("[" + Pattern.quote(symbol) + ",\\s]", "");
    if (cleanPrice.matches("\\d+.*")) {
      return new BigDecimal(cleanPrice);
    }

    return null;
  }
}
