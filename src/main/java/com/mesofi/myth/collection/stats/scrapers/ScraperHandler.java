package com.mesofi.myth.collection.stats.scrapers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Interface defining the contract for web scraping handlers that extract figurine information from
 * different online stores. Each implementation handles the specific scraping logic for a particular
 * store's website structure and data format.
 */
public interface ScraperHandler {

  // Regex pattern to extract name from the link title or text
  Pattern NAME_LINK_PATTERN = Pattern.compile("(?<=/\\d+-)(.*?)(?=-\\d+\\.html|\\.html)");

  /**
   * Get the store associated with this scraper handler.
   *
   * @return the store that this scraper handles.
   */
  Store getStore();

  /**
   * Get the search base URL.
   *
   * @return the search base URL.
   */
  String getSearchBaseUrl();

  /**
   * Get the search context URL.
   *
   * @return the search context URL.
   */
  String getSearchContextUrl();

  /**
   * Get the CSS selector for the figurines container or items.
   *
   * @return the CSS selector for the figurines.
   */
  String getFigurinesCssSelector();

  /**
   * Get the CSS selector for the figurine link.
   *
   * @return the CSS selector for the figurine link.
   */
  String getFigurineLinkCssSelector();

  /**
   * Extracts the figurine name from a product link element by parsing the href attribute. The
   * method uses a regex pattern to extract the name portion from the URL structure, then converts
   * hyphens to spaces and trims whitespace to create a readable name.
   *
   * @param productLinkElement the HTML element containing the product link
   * @return Optional containing the extracted figurine name if found, empty otherwise
   */
  default Optional<String> getFigurineName(Element productLinkElement) {
    String attributeValue = productLinkElement.attr("href");
    Matcher matcher = NAME_LINK_PATTERN.matcher(attributeValue);
    if (matcher.find()) {
      return Optional.of(matcher.group(1).replaceAll("-", " ").trim());
    }
    return Optional.empty();
  }

  /**
   * Get the CSS selector for the figurine price.
   *
   * @return the CSS selector for the figurine price.
   */
  String getFigurinePriceCssSelector();

  /**
   * Get the CSS selector for the figurine availability status.
   *
   * @return the CSS selector for the figurine availability, or null if availability checking is not
   *     supported (figurines are considered available by default)
   */
  default String getFigurineAvailabilityCssSelector() {
    return null; // by default the figurine is available.
  }

  /**
   * Retrieves and optionally filters figurine elements from the document. This method selects all
   * figurine elements using the configured CSS selector and applies filtering based on the
   * removeUnwantedFigurines() configuration. When filtering is enabled, figurines that do not
   * contain "myth cloth" or "cloth myth" in their names are removed from the collection.
   *
   * @param document the HTML document to extract figurine elements from
   * @return Elements collection containing the figurine elements, filtered if configured
   */
  default Elements getFigurineElements(Document document) {
    Elements figurines = document.select(getFigurinesCssSelector());
    if (removeUnwantedFigurines()) {
      removeUnwantedFigurines(figurines);
    }
    return figurines;
  }

  /**
   * Determines whether unwanted figurines should be removed from the collection during processing.
   * When enabled, this filtering removes figurines that do not contain "myth cloth" or "cloth myth"
   * in their names (case-insensitive), as well as figurines without extractable names.
   *
   * @return true if unwanted figurines should be filtered out, false otherwise (default behavior)
   */
  default boolean removeUnwantedFigurines() {
    return false;
  }

  /**
   * Removes unwanted figurines from the collection based on name filtering criteria. This method
   * filters out figurines that do not contain "myth cloth" or "cloth myth" in their names
   * (case-insensitive). Figurines without extractable names are also removed.
   *
   * @param figurineElement the Elements collection to filter, modified in place by removing
   *     unwanted items
   */
  private void removeUnwantedFigurines(Elements figurineElement) {
    // Filter out the results that don't contain "Myth cloth" in the name
    figurineElement.removeIf(
        element ->
            findFigurineLinkElement(element)
                .map(
                    value ->
                        getFigurineName(value)
                            .map(
                                name ->
                                    !name.toLowerCase().contains("myth cloth")
                                        && !name.toLowerCase().contains("cloth myth"))
                            .orElse(true))
                .orElse(true));
  }

  /**
   * Extracts figurine information from a figurine HTML element. This method parses the figurine
   * element to extract the product name, retail price, and product URL, then builds a
   * StoreFigurineInfo object with the extracted data.
   *
   * @param figurineElement the HTML element containing the figurine information
   * @return an Optional containing the StoreFigurineInfo object, or empty if no product link is
   *     found
   */
  default Optional<StoreFigurineInfo> extractFigurineInfo(Element figurineElement) {
    Optional<Element> optionalLinkElement = findFigurineLinkElement(figurineElement);
    if (optionalLinkElement.isEmpty()) {
      return Optional.empty();
    }

    // Extract name from the link title or text
    Optional<String> figurineName = getFigurineName(optionalLinkElement.get());
    // Extract price from the text
    Optional<BigDecimal> retailPrice = extractRetailPrice(figurineElement);
    // Extract product URL from the link href
    Optional<String> productUrl = extractFigurineUrl(optionalLinkElement.get());
    // Extract availability status from the figurine element
    Optional<Boolean> availability = isFigurineAvailable(figurineElement);

    // Build the figurine info object
    return figurineName.map(
        name ->
            StoreFigurineInfo.builder()
                .rawName(name)
                .retailPrice(retailPrice.orElse(null))
                .productUrl(productUrl.orElse(null))
                .available(availability.orElse(null))
                .build());
  }

  /**
   * Checks if a figurine is available for purchase by looking for the availability indicator
   * element. Uses the configured CSS selector to find the availability element within the figurine
   * HTML element.
   *
   * @param figurineElement the HTML element containing the figurine information
   * @return an Optional containing true if the availability element is found (indicating the
   *     figurine is available), false if not found, or empty if no availability CSS selector is
   *     configured
   */
  default Optional<Boolean> isFigurineAvailable(Element figurineElement) {
    return Optional.ofNullable(getFigurineAvailabilityCssSelector())
        .map(cssSelector -> figurineElement.selectFirst(cssSelector) != null);
  }

  /**
   * Get the currency used by the store for pricing.
   *
   * @return the currency instance, defaults to Japanese Yen
   */
  default Currency getCurrency() {
    return Currency.getInstance(Locale.JAPAN);
  }

  /**
   * Get custom HTTP headers to be used when making requests to the store.
   *
   * @return a map of custom headers, empty by default
   */
  default Map<String, String> customHeaders() {
    return Map.of();
  }

  /**
   * Finds the link element within a figurine HTML element using the configured CSS selector. This
   * method searches for the first matching element that contains the product link information.
   *
   * @param figurineElement the HTML element containing the figurine information
   * @return an Optional containing the link element, or empty if no matching element is found
   */
  private Optional<Element> findFigurineLinkElement(Element figurineElement) {
    return Optional.ofNullable(figurineElement.selectFirst(getFigurineLinkCssSelector()));
  }

  /**
   * Extracts the retail price from a figurine element. Removes currency symbols, commas, and other
   * non-numeric characters from the price text and converts it to a BigDecimal value.
   *
   * @param figurineElement the HTML element containing the figurine information
   * @return an Optional containing the retail price as BigDecimal, or empty if no valid price is
   *     found
   */
  private Optional<BigDecimal> extractRetailPrice(Element figurineElement) {
    Element priceElement = figurineElement.selectFirst(getFigurinePriceCssSelector());
    if (priceElement == null) {
      return Optional.empty();
    }

    String symbol = getCurrency().getSymbol();
    // Remove currency symbol and commas, extract numbers
    String regex = "[" + Pattern.quote(symbol) + ",\\s,a-zA-Z]";
    String cleanPrice = priceElement.text().replaceAll(regex, "");
    if (cleanPrice.matches("\\d+.*")) {
      return Optional.of(new BigDecimal(cleanPrice));
    }
    return Optional.empty();
  }

  /**
   * Extracts the product URL from a product link element. If the href attribute contains a relative
   * URL, it will be converted to an absolute URL by prepending the search base URL.
   *
   * @param productLinkElement the HTML element containing the product link
   * @return an Optional containing the product URL, or empty string if no href attribute exists
   */
  private Optional<String> extractFigurineUrl(Element productLinkElement) {
    String productUrl = "";
    if (productLinkElement.hasAttr("href")) {
      productUrl = productLinkElement.attr("href");
      if (!productUrl.startsWith("http")) {
        productUrl = getSearchBaseUrl() + productUrl;
      }
    }
    return Optional.of(productUrl);
  }
}
