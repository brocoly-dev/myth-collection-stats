package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;

/** Hobby Genki scraper handler. */
public class HobbyGenkiScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.HOBBY_GENKI;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://hobby-genki.com";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/en/search?controller=search&s=myth+cloth&page=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "article.product-miniature.js-product-miniature";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    return "a.thumbnail.product-thumbnail";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    return "span.price";
  }

  /** {@inheritDoc} */
  public boolean removeUnwantedFigurines() {
    return true;
  }
}
