package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import com.mesofi.myth.collection.stats.scrapers.ScraperDataHandler;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HobbyGenkiScraperHandler implements ScraperHandler, ScraperDataHandler {
  private static final Pattern PRICE_PATTERN = Pattern.compile("([0-9,]+(?:\\.[0-9]{2})?)");
  private static final Pattern NAME_PATTERN =
      Pattern.compile("(?<=/\\d+-)(.*?)(?=-\\d+\\.html|\\.html)");

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.HOBBY_GENKI;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchUrl() {
    return "https://hobby-genki.com/en/search?controller=search&s=myth+cloth&page=";
  }

  /** {@inheritDoc} */
  @Override
  public Elements getProductItems(Document document) {
    // Select product items from the search results
    Elements productItems = document.select("article.product-miniature");
    removeUnwantedProducts(productItems);
    return productItems;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<StoreFigurineInfo> extractFigurineInfo(Element item) {
    Element nameElement = item.selectFirst("h2.h3.product-title a");
    if (nameElement == null) {
      return Optional.empty();
    }

    // Extract name from the link title or text
    Optional<String> figurineName = findFigurineName(item);
    // Extract price from the text
    Optional<BigDecimal> retailPrice = extractRetailPrice(item);
    // Extract product URL from the link href
    Optional<String> productUrl = extractProductUrl(nameElement);

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

  private Optional<BigDecimal> extractRetailPrice(Element item) {
    Element priceElement = item.selectFirst("div.product-price-and-shipping span.price");
    if (priceElement == null) {
      return Optional.empty();
    }

    BigDecimal price;
    price = parsePrice(getCurrency(), priceElement.text());
    return price == null ? Optional.empty() : Optional.of(price);
  }

  /** {@inheritDoc} */
  public Optional<String> findFigurineName(Element item) {
    Element nameElement = item.selectFirst("h2.h3.product-title a");
    if (nameElement == null) {
      return Optional.empty();
    }
    String attributeValue = nameElement.attr("href");
    Matcher matcher = NAME_PATTERN.matcher(attributeValue);
    if (matcher.find()) {
      return Optional.of(matcher.group(1).replaceAll("-", " ").trim());
    }
    return Optional.empty();
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
