package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperDataHandler;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class LunaParkScraperHandler implements ScraperHandler, ScraperDataHandler {
  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.LUNA_PARK;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchUrl() {
    return "https://www.lunapark.store/search?q=Myth+cloth&type=products&page=";
  }

  /** {@inheritDoc} */
  @Override
  public Elements getProductItems(Document document) {
    Elements productItems = document.select("ul[aria-label='Products search results'] li");
    removeUnwantedProducts(productItems);
    return productItems;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StoreFigurineInfo> extractFigurineInfo(Element item) {
    Element nameLink = item.selectFirst("a[title]");
    if (nameLink == null) {
      return Optional.empty();
    }
    // Extract name from the link title or text
    Optional<String> figurineName = findFigurineName(item);
    // Extract price from the text
    Optional<BigDecimal> retailPrice = extractRetailPrice(item);
    // Extract product URL from the link href
    Optional<String> productUrl = extractProductUrl(nameLink);

    // Build the figurine info object
    return figurineName.map(
        name ->
            StoreFigurineInfo.builder()
                .rawName(name)
                .retailPrice(retailPrice.orElse(null))
                .productUrl(productUrl.orElse(null))
                .isAvailable(true)
                .build());
  }

  /** {@inheritDoc} */
  public Optional<String> findFigurineName(Element item) {
    Element nameLink = item.selectFirst("a[title]");
    if (nameLink == null) {
      return Optional.empty();
    }

    String name = nameLink.attr("title").trim();
    if (name.isEmpty()) {
      // Fallback to link text
      Element linkText = item.selectFirst("div[dir=auto] a");
      if (linkText != null) {
        return Optional.of(linkText.text().trim());
      }
    }
    return Optional.of(name.trim());
  }

  /**
   * Extracts the retail price from the given item.
   *
   * @param item The item to extract the retail price from.
   * @return An optional containing the retail price, or an empty optional if the price could not be
   *     extracted.
   */
  private Optional<BigDecimal> extractRetailPrice(Element item) {
    Element priceElement = item.selectFirst("span");
    if (priceElement == null) {
      return Optional.empty();
    }

    BigDecimal price;
    price = parsePrice(getCurrency(), priceElement.text());
    return price == null ? Optional.empty() : Optional.of(price);
  }

  /**
   * Extracts the product URL from the given name link.
   *
   * @param nameLink The name link to extract the product URL from.
   * @return An optional containing the product URL, or an empty optional if the URL could not be
   *     extracted.
   */
  Optional<String> extractProductUrl(Element nameLink) {
    String productUrl = "";
    if (nameLink.hasAttr("href")) {
      productUrl = nameLink.attr("href");
      if (!productUrl.startsWith("http")) {
        productUrl = "https://www.lunapark.store" + productUrl;
      }
    }
    return Optional.of(productUrl);
  }
}
