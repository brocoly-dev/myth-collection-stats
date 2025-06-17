package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.Optional;
import org.jsoup.nodes.Element;

/** Luna Park scraper handler. */
public class LunaParkScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.LUNA_PARK;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://www.lunapark.store";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/search?q=Myth+cloth&type=products&page=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "ul[aria-label='Products search results'] li";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    return "a[title]";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    return "span";
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Boolean> isFigurineAvailable(Element figurineElement) {
    return Optional.of(true);
  }

  /** {@inheritDoc} */
  public boolean removeUnwantedFigurines() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<String> getFigurineName(Element productLinkElement) {
    return Optional.of(productLinkElement.attr("title"));
  }
}
