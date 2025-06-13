package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.math.BigDecimal;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class YoyakunowScraperHandler implements ScraperHandler {
  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.YOYAKUNOW;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchUrl() {
    return "https://www.yoyakunow.com/en/search?controller=search&s=myth+cloth&page=";
  }

  /** {@inheritDoc} */
  @Override
  public Elements getProductItems(Document document) {
    // Try common selectors for product items in e-commerce sites
    Elements items = document.select("article.product-miniature");
    if (items.isEmpty()) {
      items = document.select(".product-item");
    }
    if (items.isEmpty()) {
      items = document.select(".product");
    }
    if (items.isEmpty()) {
      items = document.select("[data-id-product]");
    }
    return items;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StoreFigurineInfo> extractFigurineInfo(Element item) {
    try {
      // Extract product name
      String name = extractProductName(item);
      if (name == null || name.trim().isEmpty()) {
        return Optional.empty();
      }

      // Extract price
      BigDecimal price = extractPrice(item);
      if (price == null) {
        return Optional.empty();
      }

      // Extract product URL
      String productUrl = extractProductUrl(item);

      // Extract image URL
      String imageUrl = extractImageUrl(item);

      // Check if product is available
      boolean isAvailable = checkAvailability(item);

      return Optional.of(
          StoreFigurineInfo.builder()
              .rawName(name.trim())
              .retailPrice(price)
              // .availability(String.valueOf(isAvailable))
              .productUrl(productUrl)
              .build());
    } catch (Exception e) {
      // Log error if needed
      return Optional.empty();
    }
  }

  private String extractProductName(Element item) {
    // Try different selectors for product name
    Element nameElement = item.selectFirst("h2.product-title a");
    if (nameElement == null) {
      nameElement = item.selectFirst(".product-title");
    }
    if (nameElement == null) {
      nameElement = item.selectFirst("h3 a");
    }
    if (nameElement == null) {
      nameElement = item.selectFirst(".product-name a");
    }
    if (nameElement == null) {
      nameElement = item.selectFirst("a[title]");
    }

    if (nameElement != null) {
      String name = nameElement.text();
      if (name.isEmpty() && nameElement.hasAttr("title")) {
        name = nameElement.attr("title");
      }
      return name;
    }
    return null;
  }

  private BigDecimal extractPrice(Element item) {
    // Try different selectors for price
    Element priceElement = item.selectFirst(".price");
    if (priceElement == null) {
      priceElement = item.selectFirst(".product-price");
    }
    if (priceElement == null) {
      priceElement = item.selectFirst(".current-price");
    }
    if (priceElement == null) {
      priceElement = item.selectFirst("[data-price]");
    }

    if (priceElement != null) {
      String priceText = priceElement.text();
      if (priceText.isEmpty() && priceElement.hasAttr("data-price")) {
        priceText = priceElement.attr("data-price");
      }

      return parsePrice(priceText);
    }
    return null;
  }

  private BigDecimal parsePrice(String priceText) {
    if (priceText == null || priceText.trim().isEmpty()) {
      return null;
    }

    try {
      // Remove currency symbols and clean the price string
      String cleanPrice = priceText.replaceAll("[^0-9.,]", "").replace(",", ".");

      // Handle cases where there might be multiple dots
      int lastDotIndex = cleanPrice.lastIndexOf('.');
      if (lastDotIndex > 0 && cleanPrice.length() - lastDotIndex <= 3) {
        // Assume the last dot is decimal separator
        String integerPart = cleanPrice.substring(0, lastDotIndex).replace(".", "");
        String decimalPart = cleanPrice.substring(lastDotIndex);
        cleanPrice = integerPart + decimalPart;
      } else {
        cleanPrice = cleanPrice.replace(".", "");
      }

      return new BigDecimal(cleanPrice);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private String extractProductUrl(Element item) {
    Element linkElement = item.selectFirst("a");
    if (linkElement != null) {
      String href = linkElement.attr("href");
      if (href.startsWith("/")) {
        return "https://www.yoyakunow.com" + href;
      }
      return href;
    }
    return null;
  }

  private String extractImageUrl(Element item) {
    Element imgElement = item.selectFirst("img");
    if (imgElement != null) {
      String src = imgElement.attr("src");
      if (src.isEmpty()) {
        src = imgElement.attr("data-src");
      }
      if (src.startsWith("/")) {
        return "https://www.yoyakunow.com" + src;
      }
      return src;
    }
    return null;
  }

  private boolean checkAvailability(Element item) {
    // Check for out of stock indicators
    Elements outOfStockElements = item.select(".out-of-stock, .unavailable, .sold-out");
    if (!outOfStockElements.isEmpty()) {
      return false;
    }

    // Check for availability text
    String itemText = item.text().toLowerCase();
    return !itemText.contains("out of stock")
        && !itemText.contains("unavailable")
        && !itemText.contains("sold out");

    // Default to available if no clear indication of unavailability
  }
}
