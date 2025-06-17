package com.mesofi.myth.collection.stats.scrapers.handlers;

import com.mesofi.myth.collection.core.model.Store;
import com.mesofi.myth.collection.stats.scrapers.ScraperHandler;
import java.util.Currency;
import java.util.Locale;

/** NinNinGame scraper handler. */
public class NinNinGameScraperHandler implements ScraperHandler {

  /** {@inheritDoc} */
  @Override
  public Store getStore() {
    return Store.NIN_NIN_GAME;
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchBaseUrl() {
    return "https://www.nin-nin-game.com";
  }

  /** {@inheritDoc} */
  @Override
  public String getSearchContextUrl() {
    return "/en/search?orderby=date_add&orderway=desc&search_query=myth+cloth&submit_search=Search&p=";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinesCssSelector() {
    return "li.general_block_card.ajax_block_product.item";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurineLinkCssSelector() {
    return "a.product-name";
  }

  /** {@inheritDoc} */
  @Override
  public String getFigurinePriceCssSelector() {
    return "div.price_container span.price";
  }

  /** {@inheritDoc} */
  @Override
  public Currency getCurrency() {
    return Currency.getInstance(Locale.of("es", "MX"));
  }
}
