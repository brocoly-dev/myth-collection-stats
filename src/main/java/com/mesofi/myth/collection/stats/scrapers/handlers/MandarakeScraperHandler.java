package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class MandarakeScraperHandler implements ScraperHandler {

  private static final Pattern PRICE_PATTERN = Pattern.compile("([0-9,]+)");

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.MANDARAKE;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchUrl() {
    return "https://order.mandarake.co.jp/order/listPage/list?dispAdult=0&soldOut=1&keyword=Myth%20cloth&lang=en&deviceId=1&page=";
    // https://order.mandarake.co.jp/order/listPage/list?sort=price&sortOrder=1&dispAdult=0&soldOut=1&keyword=myth%20cloth&lang=en
  }

  /** {@inheritDoc} */
  @Override
  public Elements getProductItems(Document document) {
    // Mandarake typically uses a table structure or div containers for product listings
    Elements items = document.select("div.block");

    log.debug("Found {} product items on Mandarake page", items.size());
    return items;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StoreFigurineInfo> extractFigurineInfo(Element item) {
    try {
      // Extract title - try multiple possible selectors
      String title = extractTitle(item);
      if (title == null || title.trim().isEmpty()) {
        log.debug("No title found for item, skipping");
        return Optional.empty();
      }

      // Extract price
      String priceText = extractPrice(item);
      if (priceText == null) {
        log.debug("No price found for item: {}", title);
        return Optional.empty();
      }

      // Parse price to double
      Double price = parsePrice(priceText);
      if (price == null) {
        log.debug("Could not parse price '{}' for item: {}", priceText, title);
        return Optional.empty();
      }

      // Extract product URL
      String productUrl = extractProductUrl(item);

      // Extract image URL
      String imageUrl = extractImageUrl(item);

      // Check if item is sold out
      boolean isSoldOut = isItemSoldOut(item);

      StoreFigurineInfo figurineInfo =
          StoreFigurineInfo.builder()
              .rawName(title.trim())
              // .retailPrice(price)
              // .retailPriceText(priceText)
              // .currency("JPY") // Mandarake uses Japanese Yen
              .productUrl(productUrl)
              // .imageUrl(imageUrl)
              // .soldOut(isSoldOut)
              // .store(Store.MANDARAKE)
              .build();

      log.debug("Extracted figurine info: {} - ¥{}", title, price);
      return Optional.of(figurineInfo);

    } catch (Exception e) {
      log.error("Error extracting figurine info from Mandarake item", e);
      return Optional.empty();
    }
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> customHeaders() {
    Map<String, String> headersMap = new HashMap<>();
    headersMap.put(
        "cookie",
        "tr_mndrk_user=ec4a30d2.637387b35f151; _ga=GA1.3.1827069113.1749565585; _gid=GA1.3.105185018.1749565585; _gat=1");
    return headersMap;
  }

  private String extractTitle(Element item) {
    // Try multiple selectors for title
    Element titleElement = item.selectFirst("div.title p a:not(:empty)");
    return titleElement != null ? titleElement.text() : null;
  }

  private String extractPrice(Element item) {
    // Try multiple selectors for price
    Element priceElement = item.selectFirst("div.price p");
    return priceElement != null ? priceElement.text() : null;
  }

  private Double parsePrice(String priceText) {
    if (priceText == null || priceText.trim().isEmpty()) {
      return null;
    }

    try {
      // Remove currency symbols and extract numbers
      String cleanPrice = priceText.replaceAll("[¥円,\\s]", "");

      Matcher matcher = PRICE_PATTERN.matcher(cleanPrice);
      if (matcher.find()) {
        String priceStr = matcher.group(1).replaceAll(",", "");
        return Double.parseDouble(priceStr);
      }
    } catch (NumberFormatException e) {
      log.debug("Failed to parse price: {}", priceText, e);
    }

    return null;
  }

  private String extractProductUrl(Element item) {
    Element linkElement = item.selectFirst("div.title p a[href]");
    if (linkElement != null) {
      String href = linkElement.attr("href");
      // Convert relative URLs to absolute URLs
      if (href.startsWith("/")) {
        return "https://order.mandarake.co.jp" + href;
      }
      return href;
    }
    return null;
  }

  private String extractImageUrl(Element item) {
    Element imgElement = item.selectFirst("img[src]");
    if (imgElement != null) {
      String src = imgElement.attr("src");
      // Convert relative URLs to absolute URLs
      if (src.startsWith("/")) {
        return "https://order.mandarake.co.jp" + src;
      }
      return src;
    }
    return null;
  }

  private boolean isItemSoldOut(Element item) {
    // Check for sold out indicators
    String itemText = item.text().toLowerCase();
    return itemText.contains("sold out")
        || itemText.contains("売り切れ")
        || itemText.contains("完売")
        || item.selectFirst(".sold-out, .soldout") != null;
  }
}
