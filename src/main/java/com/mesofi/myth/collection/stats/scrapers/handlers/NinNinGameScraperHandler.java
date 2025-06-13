package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class NinNinGameScraperHandler implements ScraperHandler {
  @Override
  public Store getStore() {
    return Store.NIN_NIN_GAME;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchUrl() {
    return "https://www.nin-nin-game.com/en/search?orderby=date_add&orderway=desc&search_query=myth+cloth&submit_search=Search&p=";
  }

  /** {@inheritDoc} */
  @Override
  public Elements getProductItems(Document document) {
    // Select product items - Nin-Nin-Game uses different selectors
    Elements productItems =
        document.select("article.product-miniature, .product-item, .product_list_item");

    // Fallback selectors if the above don't work
    if (productItems.isEmpty()) {
      productItems = document.select("div[class*='product'], li[class*='product']");
    }
    return productItems;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StoreFigurineInfo> extractFigurineInfo(Element item) {
    // Extract name - try multiple selectors
    String name = extractProductName(item);
    if (name.isEmpty()) {
      return Optional.empty();
    }

    // Extract price
    String priceText = extractPriceText(item);
    BigDecimal price = parsePrice(priceText);

    // Extract product URL
    String productUrl = extractProductUrl(item);

    // Extract availability/stock status
    String availability = extractAvailability(item);

    return Optional.of(
        StoreFigurineInfo.builder()
            .rawName(name)
            .retailPrice(price)
            .productUrl(productUrl)
            // .availability(availability)
            .build());
  }

  private String extractProductName(Element item) {
    // Try different selectors for product name
    Element nameElement = item.selectFirst("h3 a, h2 a, .product-title a, .product-name a");
    if (nameElement != null) {
      String name = nameElement.text().trim();
      if (!name.isEmpty()) {
        return name;
      }
      // Try title attribute
      name = nameElement.attr("title").trim();
      if (!name.isEmpty()) {
        return name;
      }
    }

    // Fallback selectors
    nameElement = item.selectFirst("a[title], .product-title, .product-name");
    if (nameElement != null) {
      String name = nameElement.attr("title").trim();
      if (name.isEmpty()) {
        name = nameElement.text().trim();
      }
      return name;
    }

    return "";
  }

  private String extractPriceText(Element item) {
    // Try different selectors for price
    Element priceElement =
        item.selectFirst(
            ".price, .product-price, .current-price, .regular-price, span[class*='price']");

    if (priceElement != null) {
      return priceElement.text().trim();
    }

    // Fallback - look for any element containing currency symbols
    Elements priceElements = item.select("*:containsOwn(€), *:containsOwn($), *:containsOwn(¥)");
    if (!priceElements.isEmpty()) {
      return priceElements.first().text().trim();
    }

    return "";
  }

  private String extractProductUrl(Element item) {
    Element linkElement = item.selectFirst("a[href]");
    if (linkElement != null) {
      String url = linkElement.attr("href");
      if (!url.isEmpty()) {
        if (!url.startsWith("http")) {
          url = "https://www.nin-nin-game.com" + url;
        }
        return url;
      }
    }
    return "";
  }

  private String extractAvailability(Element item) {
    // Look for availability/stock information
    Element availabilityElement =
        item.selectFirst(
            ".availability, .stock-status, .product-availability, span[class*='stock']");

    if (availabilityElement != null) {
      return availabilityElement.text().trim();
    }

    // Look for common availability indicators
    Elements statusElements =
        item.select(
            "*:containsOwn(In Stock), *:containsOwn(Out of Stock), "
                + "*:containsOwn(Available), *:containsOwn(Pre-order), *:containsOwn(Sold Out)");

    if (!statusElements.isEmpty()) {
      return statusElements.first().text().trim();
    }

    return "";
  }

  private BigDecimal parsePrice(String priceText) {
    if (priceText == null || priceText.isEmpty()) {
      return null;
    }

    try {
      // Remove currency symbols, commas, and spaces, extract numbers
      String cleanPrice = priceText.replaceAll("[€$¥,\\s]", "");

      // Handle decimal prices (e.g., "29.99")
      if (cleanPrice.matches("\\d+\\.\\d+")) {
        return new BigDecimal(cleanPrice);
      }

      // Handle integer prices
      if (cleanPrice.matches("\\d+")) {
        return new BigDecimal(cleanPrice);
      }

      // Handle prices with decimal comma (e.g., "29,99")
      cleanPrice = priceText.replaceAll("[€$¥\\s]", "").replace(",", ".");
      if (cleanPrice.matches("\\d+\\.\\d+")) {
        return new BigDecimal(cleanPrice);
      }

    } catch (NumberFormatException e) {
      log.debug("Could not parse price: {}", priceText);
    }

    return null;
  }
}
